package de.bofloos.totpandroid.qrscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import de.bofloos.totpandroid.BaseActivity;
import de.bofloos.totpandroid.R;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity für das Scannen von QR-Codes.
 *
 * Rendert während des Scannens eine Preview der Kamera und gibt das Ergebnis im Intent als {@code ScanResult} mit Key
 * {@code RESULT} zurück wenn der ResultCode {@code RESULT_OK} ist.
 * Das Result kann eine URI oder einzelne Felder sein, je nachdem ob das Konto manuell angelegt worden ist.
 * <br>
 * Sobald ein QR-Code mit Text-Inhalt erkannt wird, beendet die Activity.
 * <br>
 * Fordert Permissions für die Kamera, wenn nicht schon vorhanden und gibt {@code QRScanner.ABORT_NO_PERMISSION} als
 * Result zurück, wenn keine Permissions angefordert werden konnten.
 */
public class QRScannerActivity extends BaseActivity {

    public static final String TAG = QRScannerActivity.class.getName();
    public static final int ABORT_NO_PERMISSION = 420;

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int MANUAL_REQUEST_CODE = 1337;

    Intent res;
    ProcessCameraProvider cameraProvider;
    Executor qrScannerExecutor = Executors.newSingleThreadExecutor();

    public QRScannerActivity() {
        super(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        /* Manuelle Konten-Erstellung anlegen
         * Kamera und alle gebundenen Ressourcen müssen, wegen dem Lifecycle-Binding, nicht explizit aufgeräumt werden.*/
        findViewById(R.id.manualBtn).setOnClickListener(l -> {
            Intent manualCodeIntent = new Intent(this, ManualCodeCreationActivity.class);
            startActivityForResult(manualCodeIntent, MANUAL_REQUEST_CODE);
        });

        res = getIntent();

        // Permissions anfragen, wenn keine vorhanden
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
            // Setup hier unterbrechen, bis Entscheidung über Permissions mittels onRequestPermissionsResult
            // reingekommen ist.
            return;
        }

        setup();
    }

    private void setup() {
        PreviewView previewView = findViewById(R.id.preview);

        ListenableFuture<ProcessCameraProvider> futureCameraProvider = ProcessCameraProvider.getInstance(this);
        futureCameraProvider.addListener(() -> {
            try {
                cameraProvider = futureCameraProvider.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK) // Hintere Kamera
                        .build();

                // Preview vorbereiten
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // QR Scanner vorbereiten
                ImageAnalysis qrScanner = generateQRScanner();

                // Ressourcen an Lifecycle der Activity binden, damit das Framework diese automatisch freigibt.
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, qrScanner);

            } catch (ExecutionException | InterruptedException ignored) {}
        }, ContextCompat.getMainExecutor(this));
    }

    private ImageAnalysis generateQRScanner() {
        BarcodeScannerOptions qrOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(qrOptions);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(qrScannerExecutor, image -> {
            Image mediaImage = image.getImage();
            if(mediaImage == null)
                return;
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
            // QR Code erkennen
            scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        for(Barcode code : barcodes) {
                            handleBarcode(code);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, e.getLocalizedMessage());
                    })
                    .addOnCompleteListener((_X) -> image.close());
        });

        return imageAnalysis;
    }

    /**
     * Barcode verarbeiten und Activity beenden, falls Text erkannt wurde.
     * @param code
     */
    private void handleBarcode(Barcode code) {
        // otpauth URIs werden nicht als TYPE_URL erkannt
        if(code.getValueType() != Barcode.TYPE_TEXT)
            return;

        ScanResult scanResult = new ScanResult(code.getRawValue());
        res.putExtra("RESULT", scanResult);
        setResult(RESULT_OK, res);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Überprüfen, ob angefragte Permissions bestätigt worden sind
        if(requestCode != PERMISSIONS_REQUEST_CODE
                || !Arrays.stream(grantResults).allMatch(x -> x == PackageManager.PERMISSION_GRANTED)) {
            setResult(ABORT_NO_PERMISSION, res);
            finish();
            return;
        }
        // Permission erhalten -> mit setup fortfahren
        setup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Ergebnis der ManualCodeCreationActivity zum Parent propagieren
        if(requestCode == MANUAL_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }
}
