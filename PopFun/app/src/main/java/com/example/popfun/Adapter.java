package com.example.popfun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.popfun.models.FunkoEntity;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<FunkoEntity> funkoslist;
    LayoutInflater inflater;

    public Adapter(Context ctx, List<FunkoEntity> funkoslist){
        this.funkoslist = funkoslist;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.activity_funko, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.TextoFunko.setText(funkoslist.get(position).getTextos());
        holder.DescFunko.setText(funkoslist.get(position).getDescripcion());
        holder.UserFunko.setText("       "+funkoslist.get(position).getIdUsuario());

        Glide.with(holder.FunkoImage)
                .load(funkoslist.get(position).getImagenes())
                .into(holder.FunkoImage);

    }

    @Override
    public int getItemCount(){return funkoslist.size();}

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TextoFunko;
        TextView DescFunko;
        TextView UserFunko;
        ImageView FunkoImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TextoFunko = itemView.findViewById(R.id.TextoFunko1);
            DescFunko = itemView.findViewById(R.id.TextoFunko2);
            FunkoImage = itemView.findViewById(R.id.FunkoImage1);
            UserFunko = itemView.findViewById(R.id.TextoFunko3);
        }
    }
}
