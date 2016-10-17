package codepampa.com.br.marketlist.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;

import codepampa.com.br.marketlist.R;
import codepampa.com.br.marketlist.adapter.ListAdapter;
import codepampa.com.br.marketlist.model.Produto;
import codepampa.com.br.marketlist.service.ProdutoService;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener,
        SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    private static final String TAG = "produtos";
    private ActionBar.Tab tab1, tab2;
    private EditText inputNome, inputLocalCompra, inputPreco, inputMarca;
    private ProdutoService produtoService;
    private ListView listView;
    private Produto produto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        produtoService = ProdutoService.getInstance(this);
        produto = new Produto();

        if(isTablet(this)) {
            inflarLayoutTablet();

        } else {
            inflarLayouSmartphone();
        }
    }

    private void inflarLayouSmartphone() {
        getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_TABS);
        getSupportActionBar().setTitle(R.string.text_titulo);
        tab1 = getSupportActionBar().newTab().setText("Listar");
        tab1.setTabListener(this);
        getSupportActionBar().addTab(tab1);

        tab2 = getSupportActionBar().newTab().setText("Produto");
        tab2.setTabListener(this);
        getSupportActionBar().addTab(tab2);
    }

    private void inflarLayoutTablet() {
        setContentView(R.layout.activity_tablet);
        getSupportActionBar().setTitle("Market List");
        mapearInputs();
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        getListView(produtoService.getAll());
    }


    private boolean isTablet(Context context){
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mapearInputs();
        switch (item.getItemId()){
            case R.id.new_produto:
                if(!isTablet(this)) {
                    tab2.select();
                }
                if(validarInputs()) {
                    produto.nome = inputNome.getText().toString();
                    produto.marca = inputMarca.getText().toString();
                    produto.localCompra = inputLocalCompra.getText().toString();
                    produto.preco = new BigDecimal(inputPreco.getText().toString());
                    produtoService.save(produto);
                    clear();
                    break;
                }
            case R.id.menuitem_excluir:
                if(produto._id != null) {
                    produtoService.excluir(produto);
                    clear();
                }
                break;
            case R.id.menuitem_cancelar:
                clear();
                break;
        }
        inflarSmartphoneList();
        Toast.makeText(MainActivity.this, "Operação realizada", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        switch (tab.getPosition()) {
            case 0:
                inflarSmartphoneList();
                break;
            case 1:
                setContentView(R.layout.activity_smartphone_inputs);
                mapearInputs();
                break;
            default:
                Toast.makeText(MainActivity.this, "Erro!!", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(!isTablet(this)) {
            tab1.select();
        }
        getListView(produtoService.getByName(newText));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        SearchView searchview = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();
        searchview.setQueryHint("Digite aqui ;) ");
        searchview.setOnQueryTextListener(this);
        return true;
    }

    public void getListView(List<Produto> produtos) {
        ListAdapter adapter = new ListAdapter(this, produtos);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isTablet(this)) {
            tab2.select();
        }

        mapearInputs();
        produto = (Produto) parent.getAdapter().getItem(position);
        inputNome.setText(produto.nome);
        inputMarca.setText(produto.marca);
        inputLocalCompra.setText(produto.localCompra);
        inputPreco.setText(produto.preco.toString());
        inputLocalCompra.requestFocus();

    }

    private void mapearInputs() {
        inputNome = (EditText) findViewById(R.id.input_nome_produto);
        inputPreco = (EditText) findViewById(R.id.input_preco);
        inputMarca = (EditText) findViewById(R.id.input_marca);
        inputLocalCompra = (EditText) findViewById(R.id.input_local_compra);
    }

    private void clear() {
        produto = new Produto();
        if(inputNome == null || inputPreco == null
                || inputMarca == null || inputLocalCompra == null) {
            return;
        }
            inputNome.setText("");
            inputPreco.setText("");
            inputMarca.setText("");
            inputLocalCompra.setText("");

    }

    private void inflarSmartphoneList(){
        if(!isTablet(this)) {
            tab1.select();
            setContentView(R.layout.activity_smartphone_list);
            listView = (ListView) findViewById(R.id.listView);
            listView.setOnItemClickListener(this);
        }
        getListView(produtoService.getAll());
    }

    private boolean validarInputs(){
        String msg = "";
        if (inputPreco.getText().toString().trim().isEmpty()) {
            msg += "Campo Preço obrigatorio\n";
            inputPreco.requestFocus();
        }
        if (inputMarca.getText().toString().trim().isEmpty()) {
            msg += "Campo Marca obrigatorio\n";
            inputMarca.requestFocus();
        }
        if(inputNome.getText().toString().trim().isEmpty()) {
            msg += "Campo Nome obrigatorio\n";
            inputNome.requestFocus();
        }
        if (inputLocalCompra.getText().toString().trim().isEmpty()) {
            msg += "Campo Local Compra obrigatorio\n";
            inputLocalCompra.requestFocus();
        }

        if(msg.trim().length() > 0){
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
