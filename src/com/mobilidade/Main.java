package com.mobilidade;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ActionBarActivity {

	//private AdapterListView adapterListView;
	private ArrayList<String> itens;
	private ListView lista;
	private TextView texto;
	
	private Button favoritos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		texto = (TextView) findViewById(R.id.texto);
		texto.setText("Selecione a categoria que está procurando");
		lista = (ListView) findViewById(R.id.list);
		listarCategorias();
			
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				itens) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				View view = super.getView(position, convertView, parent);
				String itemValue = itens.get(position);
				if(itemValue.contains("...")) {
					((TextView) view).setBackgroundColor(Color.BLACK);
					((TextView) view).setTextSize(22);
					((TextView) view).setTextColor(Color.WHITE);
				}else{
					((TextView) view).setBackgroundColor(Color.WHITE);
					((TextView) view).setTextSize(22);
					((TextView) view).setTextColor(Color.BLACK);
				}

				return view;
			}
		};
		
		lista.setAdapter(adapter);
		
		lista.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(!itens.get(position).contains("...")) {
					String itemValue = itens.get(position);
					Intent i = new Intent(Main.this, ExibirMapa.class);
					i.putExtra("item", itemValue);
					startActivity(i);
					finish();
				} else {
					Toast.makeText(Main.this, "Título", Toast.LENGTH_SHORT).show();
				}
								
			}
	    });
		
		favoritos = (Button)findViewById(R.id.listar_favoritos);
		favoritos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent f = new Intent(Main.this, ListarFavoritos.class);
				startActivity(f);
			}
		});
    }
 
	/**
	 * Função responável por preencher a lista com as categorias
	 */
    private void listarCategorias() {

        itens = new ArrayList<String>();
        itens.add("Compras...");
        itens.add("Super Mercados");
        itens.add("Shoppings");
        //itens.add("Lojas de Roupas");
        //itens.add("Lojas de Informática");
        //itens.add("Lojas de Brinquedos");
        //itens.add("Lojas de Suplemntos");
        //itens.add("Lojas de Roupa");
        
        itens.add("Comunitário...");
        itens.add("Postos Policiais");
        itens.add("Postos de Saúde");
        itens.add("Pessoais...");
        itens.add("Exibir Locais Favoritos");
        /*itens.add("Ensino...");
        itens.add("Pontos de Recarga de Créditos do SINETRAN");
        itens.add("Faculdades");
        itens.add("Escolas Particulares");
        
        itens.add("Lazer...");
        itens.add("Clubes");
        itens.add("Bares");
        itens.add("Casa Noturna");*/
        
        //Cria o adapter
        //adapterListView = new AdapterListView(this, itens);
 
        //Define o Adapter
        //listView.setAdapter(adapterListView);
        //Cor quando a lista é selecionada para ralagem.
        //listView.setCacheColorHint(Color.TRANSPARENT);
    }
}
