package com.mobilidade;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListaDePontos extends ActionBarActivity {

	//private ArrayList<String> itens;
	//private ListView lista;
	//private TextView texto;
	private String item;
	private ArrayList<LatLng> coordenadas;
	private ArrayList<String> titulos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_de_pontos);

		//texto = (TextView) findViewById(R.id.selecione);
		//texto.setText("Selecione o seu destino");
		//lista = (ListView) findViewById(R.id.lista);

		item = getIntent().getStringExtra("item");
		Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
		listarItens(item);

		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, titulos) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				View view = super.getView(position, convertView, parent);

				if (position % 2 == 0) {
					((TextView) view).setBackgroundColor(Color.rgb(32, 90, 70));
					((TextView) view).setTextSize(22);
					((TextView) view).setTextColor(Color.CYAN);
				} else if (position % 2 != 0) {
					((TextView) view).setBackgroundColor(Color.rgb(99, 99, 99));
					((TextView) view).setTextSize(22);
					((TextView) view).setTextColor(Color.WHITE);
				}

				return view;
			}
		};

		lista.setAdapter(adapter);

		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent(ListaDePontos.this, ExibirMapa.class);
				i.putExtra("coordenadas", coordenadas);
				i.putExtra("titulos", titulos);
				startActivity(i);
				finish();
			}
		});*/
	}

	/**
	 * 
	 * Função responsável por preencher a lista com os itens para pesquisa no
	 * mapa
	 */

	public void listarItens(String item) {

		coordenadas = new ArrayList<LatLng>();
		titulos = new ArrayList<String>();

		if (item.equals("Super Mercados")) {
			coordenadas.add(new LatLng(03.0607, 60.0130));
			titulos.add("DB ponta negra");
			coordenadas.add(new LatLng(03.0608, 60.0132));
			titulos.add("DB cidade nova");
			coordenadas.add(new LatLng(03.0609, 60.0134));
			titulos.add("Carreful Flores");
			coordenadas.add(new LatLng(03.0610, 60.0136));
			titulos.add("Carreful Paraíba");
			coordenadas.add(new LatLng(03.0611, 60.0138));
			titulos.add("Nova Era Torquato");
			
		} else if (item.equals("Shoppings")) {
			coordenadas.add(new LatLng(03.0631, 60.0138));
			titulos.add("Shopping Manauara");
			coordenadas.add(new LatLng(03.0641, 60.0131));
			titulos.add("Shopping Amazonas");
			coordenadas.add(new LatLng(03.0651, 60.0132));
			titulos.add("Shopping Plaza");
			coordenadas.add(new LatLng(03.0661, 60.0133));
			titulos.add("Shopping Milenium");
			coordenadas.add(new LatLng(03.0671, 60.0136));
			titulos.add("Shopping Studio 5");
			coordenadas.add(new LatLng(03.0681, 60.0120));
			titulos.add("Shopping Ponta Negra");
			
		} else if (item.equals("Lojas de Roupas")) {
			coordenadas.add(new LatLng(03.0601, 60.0131));
			titulos.add("Mariza Centro");
			coordenadas.add(new LatLng(03.0602, 60.0130));
			titulos.add("Riachuelo Centro");
			coordenadas.add(new LatLng(03.0603, 60.0134));
			titulos.add("D'Armand");
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lista_de_pontos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
