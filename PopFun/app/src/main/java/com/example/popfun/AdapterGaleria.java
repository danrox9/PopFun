package com.example.popfun;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.popfun.models.FunkoEntity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterGaleria extends RecyclerView.Adapter<AdapterGaleria.ViewHolder>{
    List<FunkoEntity> funkoslist;
    LayoutInflater inflater;


    public AdapterGaleria(Context ctx, List<FunkoEntity> funkoslist){
        this.funkoslist = funkoslist;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.activity_funko2, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FunkoEntity funko = funkoslist.get(position);
        holder.TextoFunko.setText(funko.getTextos());
        holder.DescFunko.setText(funko.getDescripcion());
        holder.UserFunko.setText("By: "+funko.getIdUsuario());
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarPublicacion.class);
                intent.putExtra("texto", funko.getTextos());
                intent.putExtra("descripcion", funko.getDescripcion());
                intent.putExtra("imagen", funko.getImagenes());
                view.getContext().startActivity(intent);
            }
        });

        Picasso.with(inflater.getContext())
                .load(funko.getImagenes())
                .into(holder.FunkoImage);
    }


    @Override
    public int getItemCount(){return funkoslist.size();}

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TextoFunko;
        TextView DescFunko;
        TextView UserFunko;
        ImageView FunkoImage;
        ImageView editar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TextoFunko = itemView.findViewById(R.id.TextoFunko1);
            DescFunko = itemView.findViewById(R.id.TextoFunko2);
            FunkoImage = itemView.findViewById(R.id.FunkoImage1);
            UserFunko = itemView.findViewById(R.id.TextoFunko3);
            editar = itemView.findViewById(R.id.editarfunko);

        }
    }
}

