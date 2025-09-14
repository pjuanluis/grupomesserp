package com.grupomess.erp.ui.gallery;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupomess.erp.R;

import java.util.List;

/**
 * Adaptador para mostrar una lista de miniaturas de fotos en un RecyclerView.
 * Permite eliminar fotos mediante un botón en cada elemento.
 *
 * Uso principal:
 * - Mostrar imágenes (Bitmap) en una lista horizontal.
 * - Permitir al usuario eliminar una foto específica.
 *
 * @author SOLTICSS
 * @since 2025
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    /**
     * Lista de imágenes a mostrar.
     */
    private final List<Bitmap> photos;
    /**
     * Listener para manejar la eliminación de una foto.
     */
    private final OnPhotoDeleteListener deleteListener;

    /**
     * Interfaz para notificar la eliminación de una foto.
     */
    public interface OnPhotoDeleteListener {
        /**
         * Se llama cuando se solicita eliminar una foto.
         * @param position posición de la foto en la lista
         */
        void onPhotoDelete(int position);
    }

    /**
     * Constructor del adaptador.
     * @param photos lista de imágenes a mostrar
     * @param deleteListener listener para manejar la eliminación
     */
    public PhotosAdapter(List<Bitmap> photos, OnPhotoDeleteListener deleteListener) {
        this.photos = photos;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_thumbnail, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.imageView.setImageBitmap(photos.get(position));
        holder.deleteButton.setOnClickListener(v -> deleteListener.onPhotoDelete(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    /**
     * ViewHolder para cada miniatura de foto.
     */
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        /** Imagen miniatura. */
        ImageView imageView;
        /** Botón para eliminar la foto. */
        ImageButton deleteButton;

        /**
         * Constructor del ViewHolder.
         * @param itemView vista del elemento
         */
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnailImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
