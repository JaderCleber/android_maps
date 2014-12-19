package com.mobilidade;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListarFavoritos extends ActionBarActivity {

	private ArrayList<String> favoritos = new ArrayList<String>();
	private ArrayList<String> des = new ArrayList<String>();
	private ArrayList<String> leitura = new ArrayList<String>();
	private ListView lista;
	private double lat[] = null;
	private double lon[] = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listar_favoritos);
		
		lista = (ListView) findViewById(R.id.lista_favoritos);
		
		leitura = acessaInternalStorage();
		for (int i = 0; i < leitura.size(); i += 4) {
			//titulos[i] = leitura.get(i);
			//lat[i+1] = Double.parseDouble(leitura.get(i + 1));
			//lon[i+2] = Double.parseDouble(leitura.get(i + 2));
			//des[i+3] = leitura.get(i+3);
			
			favoritos.add(leitura.get(i));
			des.add(leitura.get(i+3));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				favoritos) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				View view = super.getView(position, convertView, parent);
				String itemValue = favoritos.get(position);
				if(position % 2 == 0) {
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
				
				try{
					AlertDialog.Builder builder = new AlertDialog.Builder(ListarFavoritos.this);
					builder.setTitle(favoritos.get(position));
					builder.setMessage(des.get(position));
					builder.setCancelable(false);
	
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}catch(Exception e){}
			}
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.listar_favoritos, menu);
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
	
	private ArrayList<String> acessaInternalStorage() {

		String line;
		String conteudo = "";
		ArrayList<String> favoritos = new ArrayList<String>();

		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(getFilesDir().getPath()
					+ "/Locais_Favoritos.txt"));

			while ((line = br.readLine()) != null) {
				favoritos.add(line);
				// favoritos.add(conteudo);
			}

		} catch (Exception e) {
			Toast.makeText(this, "Nenhum local salvo como favorito",
					Toast.LENGTH_SHORT).show();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					Toast.makeText(this, "Erro ocorreu ao fechar",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		return favoritos;
	}
}
