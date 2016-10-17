package codepampa.com.br.marketlist.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import codepampa.com.br.marketlist.R;
import codepampa.com.br.marketlist.model.Produto;


public class ListAdapter extends ArrayAdapter<Produto> {


    public ListAdapter(Context context, List<Produto> produtos) {
        super(context, 0, produtos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Produto produto = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_layout, parent, false);
        }

        TextView tvNomeProduto = (TextView) convertView.findViewById(R.id.tv_item_nome_produto);
        TextView tvNomeMarca = (TextView) convertView.findViewById(R.id.tv_item_marca);
        TextView tvLocalCompra = (TextView) convertView.findViewById(R.id.tv_item_nome_local_compra);
        TextView tvPreco = (TextView) convertView.findViewById(R.id.tv_item_preco);
        // popula as views
        tvNomeProduto.setText(produto.nome);
        tvNomeMarca.setText(produto.marca);
        tvLocalCompra.setText(produto.localCompra);
        tvPreco.setText(produto.preco.toString());

        ImageView imvImagem = (ImageView) convertView.findViewById(R.id.imv_item);
        if (produto.imagem != null) {
            //converte byte[] para Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(produto.imagem, 0, produto.imagem.length);
            //carrega a imagem na ImageView do item da ListView
            imvImagem.setImageBitmap(bitmap);
        } else {
            //carrega a imagem padrão (se não houver imagem no Cursor)
            imvImagem.setImageResource(R.drawable.sacola);
        }

        return convertView;
    }

}