package com.renovationapps.cuentosprincesas.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.renovationapps.cuentosprincesas.R;

import java.util.ArrayList;

public class RecomendedAdapter extends RecyclerView.Adapter<RecomendedAdapter.MyViewHolder>{
    private Context mContext ;
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<String> titulo = new ArrayList<String>();
    ArrayList<String> precio = new ArrayList<String>();
    ArrayList<String> descripcion = new ArrayList<String>();
    ArrayList<String> cantidad = new ArrayList<String>();
    ArrayList<String> id = new ArrayList<String>();

    public RecomendedAdapter(Context mContext, ArrayList images, ArrayList titulo, ArrayList id, ArrayList precio, ArrayList descripcion, ArrayList cantidad) {
        this.mContext = mContext;
        this.images = images;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.id = id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_recomended,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_book_title.setText(titulo.get(position));

        holder.tv_book_precio.setText(precio.get(position));

        holder.tv_book_descripcion.setText(descripcion.get(position));

        holder.tv_book_cantidad.setText(cantidad.get(position));

        Glide.with(mContext).load(images.get(position)).into(holder.img_book_thumbnail);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(titulo.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);

            }
        });

        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext,
                        "Agregado al carrito", Toast.LENGTH_LONG).show();

            }
        });





    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_book_title;
        TextView tv_book_precio;
        TextView tv_book_descripcion;
        TextView tv_book_cantidad;
        FloatingActionButton fab;
        ImageView img_book_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_book_title =  itemView.findViewById(R.id.book_title_id) ;
            tv_book_precio =  itemView.findViewById(R.id.lbl_precio);
            tv_book_descripcion =  itemView.findViewById(R.id.lbl_descripcion);
            tv_book_cantidad =  itemView.findViewById(R.id.lbl_cantidad);
            img_book_thumbnail =  itemView.findViewById(R.id.book_img_id);
            cardView =  itemView.findViewById(R.id.cardview_id);
            fab = itemView.findViewById(R.id.fab);

        }
    }
}
