package com.grupomess.erp.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.grupomess.erp.R;
import com.grupomess.erp.databinding.FragmentFolioBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Fragmento que permite:
 * - Escanear texto de una foto usando ML Kit (folio).
 * - Capturar múltiples fotos y mostrarlas en un RecyclerView.
 * - Guardar las fotos en la carpeta Descargas, agrupadas por folio.
 * - Eliminar fotos antes de guardar.
 *
 * Flujo principal:
 * 1. El usuario escanea el folio (texto) desde una foto.
 * 2. El usuario captura varias fotos relacionadas al folio.
 * 3. El usuario guarda las fotos localmente, agrupadas por el nombre del folio.
 */
public class FolioFragment extends Fragment {

    /**ViewBinding para acceder a los elementos de la UI */
    private FragmentFolioBinding binding;
    /** Botón para escanear folio (texto) */
    private ImageButton scanButton, multiCaptureButton;
    /** Botón para guardar fotos localmente */
    private Button saveButton;
    // Campo de texto para mostrar el folio escaneado
    private EditText folioEditText;
    // Texto que muestra el número de fotos capturadas
    private TextView photosCountTextView;
    // RecyclerView para mostrar miniaturas de fotos capturadas
    private RecyclerView photosRecyclerView;

    /** Lista de fotos capturadas **/
    private ArrayList<Bitmap> capturedPhotos = new ArrayList<>();
    /** Adaptador para el RecyclerView de fotos */
    private PhotosAdapter adapter;

    /** Lanzadores para permisos y cámara */
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    /**Indica si está en modo captura múltiple */
    private boolean isMultiCapture = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa el lanzador para solicitar permiso de cámara
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                });

        // Inicializa el lanzador para abrir la cámara y recibir la foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

                        if (isMultiCapture) {
                            // Agrega la foto a la lista y actualiza la UI
                            capturedPhotos.add(bitmap);
                            updatePhotosCount();
                            adapter.notifyDataSetChanged();
                        } else {
                            // Escanea el texto del folio desde la imagen
                            scanTextFromImage(bitmap);
                        }
                    }
                });
    }

    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                          android.view.ViewGroup container,
                                          Bundle savedInstanceState) {

        new ViewModelProvider(this).get(FolioViewModel.class);
        binding = FragmentFolioBinding.inflate(inflater, container, false);

        // UI
        scanButton = binding.getRoot().findViewById(R.id.scanButton);
        multiCaptureButton = binding.getRoot().findViewById(R.id.multiCaptureButton);
        saveButton = binding.getRoot().findViewById(R.id.saveButton);
        folioEditText = binding.getRoot().findViewById(R.id.folioEditText);
        photosCountTextView = binding.getRoot().findViewById(R.id.photosCountTextView);
        photosRecyclerView = binding.getRoot().findViewById(R.id.photosRecyclerView);

        // Configura RecyclerView con adapter y listener de eliminar
        adapter = new PhotosAdapter(capturedPhotos, position -> {
            capturedPhotos.remove(position);
            updatePhotosCount();
            adapter.notifyDataSetChanged();
        });
        photosRecyclerView.setAdapter(adapter);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Botón escanear folio
        scanButton.setOnClickListener(v -> {
            isMultiCapture = false;
            checkCameraPermission();
        });

        // Botón capturar múltiples fotos
        multiCaptureButton.setOnClickListener(v -> {
            isMultiCapture = true;
            checkCameraPermission();
        });

        // Botón guardar fotos
        saveButton.setOnClickListener(v -> savePhotosLocally());

        updatePhotosCount();

        return binding.getRoot();
    }

    /**
     * Verifica el permiso de cámara y lo solicita si es necesario.
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Abre la cámara para capturar una foto.
     */
    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Usa ML Kit para escanear texto (folio) desde una imagen.
     * @param bitmap Imagen capturada
     */
    private void scanTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> folioEditText.setText(visionText.getText()))
                .addOnFailureListener(e -> {
                    Log.e("FolioFragment", "Error al escanear", e);
                    Toast.makeText(getContext(), "Error al escanear: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Actualiza el contador de fotos capturadas en la UI.
     */
    private void updatePhotosCount() {
        photosCountTextView.setText("Fotos capturadas: " + capturedPhotos.size());
    }

    /**
     * Guarda las fotos capturadas localmente en la carpeta Descargas, agrupadas por folio.
     * Limpia la lista de fotos y el campo de folio tras guardar.
     */
    private void savePhotosLocally() {
        if (capturedPhotos.isEmpty()) {
            Toast.makeText(getContext(), "No hay fotos para guardar", Toast.LENGTH_SHORT).show();
            return;
        }
        String folioName = folioEditText.getText().toString().trim();
        if (folioName.isEmpty()) {
            Toast.makeText(getContext(), "No hay folio capturado", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < capturedPhotos.size(); i++) {
            String fileName = folioName + "_foto_" + (i + 1) + ".jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + folioName);

            Uri uri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (FileOutputStream out = (FileOutputStream) requireContext().getContentResolver().openOutputStream(uri)) {
                    capturedPhotos.get(i).compress(Bitmap.CompressFormat.JPEG, 90, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        capturedPhotos.clear();
        updatePhotosCount();
        adapter.notifyDataSetChanged();
        folioEditText.setText("");

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Fotos guardadas")
                .setMessage("Se guardaron las fotos en la carpeta Descargas/" + folioName)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
