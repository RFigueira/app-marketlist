package codepampa.com.br.marketlist.service;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import codepampa.com.br.marketlist.model.Produto;

public class ProdutoService extends SQLiteOpenHelper{

    private static final String NOME_BD = "produto.sqlite";
    private static final int VERSAO = 1;
    private static ProdutoService produtoService = null;

    private ProdutoService(Context context) {
        super(context, NOME_BD, null, VERSAO);
        getWritableDatabase();
    }

    public static ProdutoService getInstance(Context context){
        if(produtoService == null){
            produtoService = new ProdutoService(context);
        }
        return produtoService;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS produto " +
                "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT, " +
                "marca TEXT, " +
                "preco NUMERIC, " +
                "localCompra TEXT, " +
                "imagem BLOB );";
        db.execSQL(sql);
        Log.d("create database", "tabela criada");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long save(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("nome",produto.nome);
            values.put("marca",produto.marca);
            values.put("preco", produto.preco.doubleValue());
            values.put("localCompra",produto.localCompra);
            values.put("imagem",produto.imagem);

            if(produto._id == null) {
                return db.insert("produto",null, values);
            }

            values.put("_id",produto._id);
            return db.update("produto", values, " _id=" + produto._id, null);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
        return 0;
    }

    public List<Produto> getAll(){
        SQLiteDatabase db = getReadableDatabase();
        try {
            //retorna uma List para os registros contidos no banco de dados
            // select * from carro
            return toList(db.rawQuery("SELECT  * FROM produto", null));
        } finally {
            db.close();
        }
    }

    private List<Produto> toList(Cursor c) {
        List<Produto> contatos = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                Produto contato = new Produto();

                // recupera os atributos do cursor para produto
                contato._id = c.getLong(c.getColumnIndex("_id"));
                contato.nome = c.getString(c.getColumnIndex("nome"));
                contato.marca = c.getString(c.getColumnIndex("marca"));
                contato.preco = new BigDecimal(c.getString(c.getColumnIndex("preco")));
                contato.localCompra = c.getString(c.getColumnIndex("localCompra"));
                contato.imagem = c.getBlob(c.getColumnIndex("imagem"));

                contatos.add(contato);

            } while (c.moveToNext());
        }

        return contatos;
    }

    public long excluir(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete("produto", "_id=?", new String[]{String.valueOf(produto._id)});
        } finally {
            db.close();
        }
    }

    public List<Produto> getByName(String nome) {
        SQLiteDatabase db = getWritableDatabase();
        //|nome = nome.toLowerCase();
        try {
            return toList(db.rawQuery("SELECT * FROM  produto WHERE lower(nome) LIKE '" + nome.toLowerCase() + "%'", null));
        } finally {
            db.close();
        }
    }
}
