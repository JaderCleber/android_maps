package com.mobilidade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExibirMapa extends FragmentActivity implements
		OnMarkerClickListener {

	private GoogleMap mapa;

	private String item;
	private ArrayList<LatLng> coordenadas;
	private ArrayList<String> titulos;
	private ArrayList<Marker> listaMarcadores = new ArrayList<Marker>();

	private double latitude;
	private double longitude;

	private LatLng destino;
	private boolean rota;

	private String nomeFavorito="";
	private String descricao = "";
	private TextView informacoes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exibir_mapa);

		item = getIntent().getStringExtra("item");
		//Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
		listarItens(item);
		informacoes = (TextView) findViewById(R.id.informacoes);

		geolocalizacao();
		desenharMapa();
	}

	public void desenharMapa() {

		if (mapa != null) {
			mapa.clear();
		}
		mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapa))
				.getMap();

		mapa.getUiSettings().setMyLocationButtonEnabled(true);
		mapa.getUiSettings().setCompassEnabled(true);
		mapa.setMyLocationEnabled(true);
		mapa.setOnMarkerClickListener(this);

		for (int i = 0; i < coordenadas.size(); i++) {
			Marker frameworkSystem = mapa.addMarker(new MarkerOptions()
					.position(coordenadas.get(i)).title(titulos.get(i)));
			listaMarcadores.add(frameworkSystem);
		}
		rota = false;
	}

	public void geolocalizacao() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
				new LocationListener() {

					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {
						// Toast.makeText(getApplicationContext(),
						// "Status alterado", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onProviderEnabled(String arg0) {
						Toast.makeText(getApplicationContext(),
								"GPS Habilitado", Toast.LENGTH_LONG).show();

					}

					@Override
					public void onProviderDisabled(String arg0) {
						Toast.makeText(getApplicationContext(),
								"Habilite o GPS", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onLocationChanged(Location location) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(latitude, longitude), 5));
						mapa.animateCamera(CameraUpdateFactory.zoomTo(15),
								2000, null);
					}
				}, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.atualizar:
			if (rota) {
				geolocalizacao();
				new RotaAsyncTask(ExibirMapa.this, mapa).execute(latitude,
						longitude, destino.latitude, destino.longitude);
			}
			return true;
		case R.id.pontos:
			mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					latitude, longitude), 18));
			mapa.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
			return true;
		case R.id.limpar:
			informacoes.setText("");
			desenharMapa();
			mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					latitude, longitude), 18));
			mapa.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
			return true;
		case R.id.salvarLocal:
			inserirTitulo();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String inserirTitulo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Informe Um Título Para Identificar Este Local");
		builder.setCancelable(false);

		final EditText inserir = new EditText(this);
		builder.setView(inserir);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				nomeFavorito = inserir.getText().toString();
				inserirDescricao();
			}
		})

		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				nomeFavorito = "";
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
		return nomeFavorito;
	}

	private String inserirDescricao(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Informe Uma Descrição");
		builder.setCancelable(false);

		final EditText in = new EditText(this);
		builder.setView(in);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				descricao = in.getText().toString();
				// salvarSharedPref(descricao, latitude, longitude);
				salvarInternalStorage(nomeFavorito, latitude, longitude, descricao);
				// salvarExternalStorage(descricao, latitude, longitude);
			}
		})

		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				nomeFavorito = "";
				descricao = "";
				dialog.cancel();
			}
			
		});
		AlertDialog alert = builder.create();
		alert.show();	
		return descricao;
	}
	/**
	 * 
	 * Função responsável por preencher a lista com os itens para pesquisa no
	 * mapa
	 * 
	 */

	public void listarItens(String item) {

		coordenadas = new ArrayList<LatLng>();
		titulos = new ArrayList<String>();

		if (item.equals("Exibir Locais Favoritos")) {
			//String x = "";
			ArrayList<String> leitura = acessaInternalStorage();
			for (int i = 0; i < leitura.size(); i += 4) {
				String des = leitura.get(i);
				double lat = Double.parseDouble(leitura.get(i + 1));
				double lon = Double.parseDouble(leitura.get(i + 2));
				//x += des + "\n" + lat + "\n" + lon+"\n\n";
				coordenadas.add(new LatLng(lat, lon));
				titulos.add(des);
			}
			//Toast.makeText(this, x,Toast.LENGTH_SHORT).show();

			/*
			 * SharedPreferences prefs = getSharedPreferences(
			 * "LOCAIS_FAVORITOS_MOBILIDADE", Context.MODE_PRIVATE); String des
			 * = prefs.getString("DESCRICAO", "no find"); String lt =
			 * prefs.getString("LATITUDE", "0");
			 */
			// return (texto + "n" + numero);

		} else if (item.equals("Super Mercados")) {
			/*BufferedReader br = null;
			try {
				InputStream is = new FileInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
				String s = "";

				while ((s = br.readLine()) != null) {
					String des = s.toString();
					double lat = Double.parseDouble(s = br.readLine()
							.toString());
					double lon = Double.parseDouble(s = br.readLine()
							.toString());
					Toast.makeText(this, des + "\n" + lat + "\n" + lon,
							Toast.LENGTH_SHORT).show();
					coordenadas.add(new LatLng(lat, lon));
					titulos.add(des);
				}

			} catch (Exception e) {
				Toast.makeText(this, "Erro ao ler arquivo de dadoss",
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
			}*/

			coordenadas.add(new LatLng(-3.091402, -60.009274));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.085326, -60.025845));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.120533, -59.980842));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.075629, -59.957152));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.030536, -59.977381));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.089651, -60.056693));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.003111, -59.980775));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.089935, -60.058018));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.068050, -60.095520));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.036052, -60.000770));
			titulos.add("DB");
			coordenadas.add(new LatLng(-3.0305362, -59.9773801));
			titulos.add("DB");

			coordenadas.add(new LatLng(-3.0878749, -60.0074905));
			titulos.add("DB");

			coordenadas.add(new LatLng(-3.0878749, -60.0084908));
			titulos.add("DB");

			coordenadas.add(new LatLng(-3.105379, -60.011047));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.132939, -60.023517));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.132757, -60.022943));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.133153, -60.024053));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.094077, -60.021944));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.085846, -60.070439));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.092824, -60.049878));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.079097, -60.024368));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.112887, -60.023580));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.079111, -60.024367));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.092947, -60.051484));
			titulos.add("Carrefour");
			coordenadas.add(new LatLng(-3.042510, -60.009398));
			titulos.add("Atack");
			coordenadas.add(new LatLng(-3.121601, -60.010752));
			titulos.add("Atack");
			coordenadas.add(new LatLng(-3.120861, -60.011774));
			titulos.add("Mercadinho Aguiar");
			coordenadas.add(new LatLng(-3.119854, -60.009843));
			titulos.add("Supermercados Bastos");
			coordenadas.add(new LatLng(-3.124235, -60.011710));
			titulos.add("Mercadinho União");
			coordenadas.add(new LatLng(-3.126425, -60.010076));
			titulos.add("Bom Preço");
			coordenadas.add(new LatLng(-3.111430, -60.015047));
			titulos.add("Emporium Roma");
			coordenadas.add(new LatLng(-3.122911, -60.020712));
			titulos.add("Supermercado Fuji");
			coordenadas.add(new LatLng(-3.117482, -60.017715));
			titulos.add("A F Lima Mercadoria");
			coordenadas.add(new LatLng(-3.115896, -60.004369));
			titulos.add("Supermercados Nova Aliança");
			coordenadas.add(new LatLng(-3.136465, -60.012050));
			titulos.add("Makro");
			coordenadas.add(new LatLng(-3.117368, -60.020781));
			titulos.add("Mercadinho Ayrão");
			coordenadas.add(new LatLng(-3.135908, -60.017329));
			titulos.add("Pare Leve Ltda");
			coordenadas.add(new LatLng(-3.107969, -60.015956));
			titulos.add("Pareleve Adrianópolis Manaus");
			coordenadas.add(new LatLng(-3.118981, -60.025222));
			titulos.add("Mercadinho Santo Expedito");
			coordenadas.add(new LatLng(-3.130351, -59.991657));
			titulos.add("Mercadinho SP Baratão da Carne");
			coordenadas.add(new LatLng(-3.113059, -60.026647));
			titulos.add("Empório Santa Fé");
			coordenadas.add(new LatLng(-3.125915, -60.027162));
			titulos.add("Casa de Embalagens Campina");
			coordenadas.add(new LatLng(-3.138479, -60.008446));
			titulos.add("Loja Bailo");
			coordenadas.add(new LatLng(-3.139325, -60.008156));
			titulos.add("T.N. Farias Comércio de Mercadorias");
			coordenadas.add(new LatLng(-3.139783, -60.001952));
			titulos.add("Mercadinho Terra boa");
			coordenadas.add(new LatLng(-3.133969, -59.996013));
			titulos.add("Supermercado da Família");
			coordenadas.add(new LatLng(-3.128020, -60.029203));
			titulos.add("Mercado Ossami®");
			coordenadas.add(new LatLng(-3.132972, -59.993597));
			titulos.add("Mercadinho Pantanal");
			coordenadas.add(new LatLng(-3.113236, -59.991999));
			titulos.add("Mercadinho Joelma");
			coordenadas.add(new LatLng(-3.135190, -60.026455));
			titulos.add("Supermercado Vitória");
			coordenadas.add(new LatLng(-3.102741, -59.990593));
			titulos.add("Supermercados Resende");
			coordenadas.add(new LatLng(-3.104026, -59.993125));
			titulos.add("Mercadinho e Açougue Moraes");
			coordenadas.add(new LatLng(-3.100834, -59.989820));
			titulos.add("Mercadinho Vale do Sol");
			coordenadas.add(new LatLng(-3.100658, -59.990974));
			titulos.add("Mercadinho Fortaleza");
			coordenadas.add(new LatLng(-3.102094, -59.986264));
			titulos.add("Mercadinho, Panificadora e Açougue Diuliane");
			coordenadas.add(new LatLng(-3.069739, -60.005889));
			titulos.add("Supermercado Veneza");
			coordenadas.add(new LatLng(-3.106893, -60.046882));
			titulos.add("Supermercado Robson");
			coordenadas.add(new LatLng(-3.090512, -59.985995));
			titulos.add("Mercadinho Adão e Eva");
			coordenadas.add(new LatLng(-3.070798, -60.016049));
			titulos.add("Pareleve Flores Manaus");

		} else if (item.equals("Shoppings")) {
			coordenadas.add(new LatLng(-3.101122, -60.025088));
			titulos.add("Millennium Shopping");
			coordenadas.add(new LatLng(-3.097523, -60.022181));
			titulos.add("Manaus Plaza Shopping");
			coordenadas.add(new LatLng(-3.094239, -60.022535));
			titulos.add("Amazonas Shopping");
			coordenadas.add(new LatLng(-3.104753, -60.010657));
			titulos.add("Manauara Shopping");
			coordenadas.add(new LatLng(-3.031335, -59.984927));
			titulos.add("Shopping Samauma");
			coordenadas.add(new LatLng(-3.124483, -59.983383));
			titulos.add("Studio 5");
			coordenadas.add(new LatLng(-3.062121, -59.949806));
			titulos.add("Grande Circular");
			coordenadas.add(new LatLng(-3.075474, -59.956800));
			titulos.add("Uai");

		} else if (item.equals("Postos Policiais")) {
			coordenadas.add(new LatLng(-3.053229, -60.039934));
			titulos.add("17º DIP");
			coordenadas.add(new LatLng(-3.090388, -59.981490));
			titulos.add("11º DIP");
			coordenadas.add(new LatLng(-3.114480, -59.998992));
			titulos.add("3º DIP");
			coordenadas.add(new LatLng(-3.086084, -60.067954));
			titulos.add("19º DIP");
			coordenadas.add(new LatLng(-3.119024, -60.047190));
			titulos.add("5º DIP");
			coordenadas.add(new LatLng(-3.005923, -60.037695));
			titulos.add("20º DIP");
			coordenadas.add(new LatLng(-3.138609, -59.992344));
			titulos.add("7º DIP");
			coordenadas.add(new LatLng(-3.072645, -59.948117));
			titulos.add("9º DIP");
			coordenadas.add(new LatLng(-3.147549, -60.005513));
			titulos.add("2º DIP");
			coordenadas.add(new LatLng(-3.050185, -59.945048));
			titulos.add("14º DIP");
			coordenadas.add(new LatLng(-3.090854, -59.943494));
			titulos.add("25º DIP");
			coordenadas.add(new LatLng(-3.044057, -59.959318));
			titulos.add("27º DIP");
			coordenadas.add(new LatLng(-3.103545, -60.056868));
			titulos.add("8º DIP");
		} else if (item.equals("Postos de Saúde")) {
			coordenadas.add(new LatLng(-3.084901, -60.072699));
			titulos.add("SPA Prodmed");
			coordenadas.add(new LatLng(-3.104949, -60.065208));
			titulos.add("SPA Joventina Dias");
			coordenadas.add(new LatLng(-3.080952, -60.010593));
			titulos.add("Policlinica Castelo Branco");
			coordenadas.add(new LatLng(-3.135845, -59.994757));
			titulos.add("Policlinica Dr. Antonio Reis");
			coordenadas.add(new LatLng(-3.051425, -60.046213));
			titulos.add("Policlinica Redenção Dr. José Lins");
			coordenadas.add(new LatLng(-3.129461, -60.0266));
			titulos.add("Policlinica Av. Epaminondas");
			coordenadas.add(new LatLng(-3.119862, -60.003083));
			titulos.add("Policlinica Codajas");
			coordenadas.add(new LatLng(-3.055796, -59.947636));
			titulos.add("Policlinica Zeno Lanzine");
			coordenadas.add(new LatLng(-3.133531, -60.025699));
			titulos.add("Policlinica Cardoso Fontes");
			coordenadas.add(new LatLng(-3.131603, -60.020742));
			titulos.add("Policlinica Governador Gilberto Mestrinho");
			coordenadas.add(new LatLng(-3.035955, -59.955683));
			titulos.add("Policlinica");
			coordenadas.add(new LatLng(-3.107435, -60.061941));
			titulos.add("Policlinica DR DJALMA BATISTA");
			coordenadas.add(new LatLng(-3.074181, -59.962893));
			titulos.add("Policlinica Enfª Ivone Lima dos Santos");
			coordenadas.add(new LatLng(-3.075038, -60.057478));
			titulos.add("Policlinica Dr. José Raimundo Franco de Sá");
			coordenadas.add(new LatLng(-2.997727, -59.995165));
			titulos.add("Policlinica José Antônio da Silva");
			coordenadas.add(new LatLng(-3.029441, -59.977312));
			titulos.add("Policlinica Danilo Corrêa");
			coordenadas.add(new LatLng(-3.004756, -59.977999));
			titulos.add("Policlinica João dos Santos Braga");
			coordenadas.add(new LatLng(-3.023441, -59.933023));
			titulos.add("Policlinica Enfª Ana Barreto");
			coordenadas.add(new LatLng(-3.033212, -59.921179));
			titulos.add("Policlinica Dr. José Avelino Pereira");
		}
	}

	public void onBackPressed() {
		Intent i = new Intent(this, Main.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Traçar Rota Para "
				+ marker.getTitle().toUpperCase());
		builder.setCancelable(false);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				destino = marker.getPosition();
				informacoes.setText("Rota traçada para " + marker.getTitle());
				desenharMapa();
				new RotaAsyncTask(ExibirMapa.this, mapa).execute(latitude,
						longitude, destino.latitude, destino.longitude);
				mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						latitude, longitude), 15));
				mapa.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
				rota = true;
			}
		})

		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

		return true;
	}

	private void salvarSharedPref(String descricao, double lat, double lon) {

		SharedPreferences prefs = getSharedPreferences(
				"LOCAIS_FAVORITOS_MOBILIDADE", Context.MODE_PRIVATE);
		Editor ed = prefs.edit();

		ed.putString("DESCRICAO", descricao);
		ed.putString("LATITUDE", "" + lat);
		ed.putString("LONGITUDE", "" + lon);
		ed.commit();
		Toast.makeText(this, "Local Salvo Na Lista De Favoritos",
				Toast.LENGTH_SHORT).show();
	}

	private void salvarInternalStorage(String titulo, double lat, double lon, String descricao) {
		FileWriter fileWriter = null;

		try {
			File file = new File(getFilesDir().getPath()
					+ "/Locais_Favoritos.txt");
			fileWriter = new FileWriter(file, true);

			fileWriter.append(titulo);
			fileWriter.append("\n");
			fileWriter.append(String.valueOf(lat));
			fileWriter.append("\n");
			fileWriter.append(String.valueOf(lon));
			fileWriter.append("\n");
			fileWriter.append(descricao);
			fileWriter.append("\n");

			fileWriter.flush();
			Toast.makeText(this, "Local Salvo Na Lista De Favoritos",
					Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			Log.e("Erros", "Erro ao salvar usando Internal Storage", e);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private void salvarExternalStorage(String descricao, double lat, double lon) {

		String mediaState = Environment.getExternalStorageState();

		if (mediaState.equals(Environment.MEDIA_MOUNTED)) {

			FileWriter fileWriter = null;
			try {

				File file = new File(getExternalFilesDir(null).getPath(),
						"/Locais_Favoritos_Mobilidade_Ext.txt");
				fileWriter = new FileWriter(file, true);

				fileWriter.append(descricao);
				fileWriter.append("\n");
				fileWriter.append(String.valueOf(lat));
				fileWriter.append("\n");
				fileWriter.append(String.valueOf(lon));

				fileWriter.flush();
				Toast.makeText(this, "Local Salvo Na Lista De Favoritos",
						Toast.LENGTH_SHORT).show();

			} catch (IOException e) {
				Log.e("Erros", "Erro ao salvar usando External Storage", e);
			} finally {
				if (fileWriter != null) {
					try {
						fileWriter.close();
					} catch (Exception e) {
					}
				}
			}
		}
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

	private ArrayList<String> acessaExternalStorage() {

		String line;
		String conteudo = "";
		ArrayList<String> favoritos = new ArrayList<String>();

		BufferedReader br = null;
		String mediaState = Environment.getExternalStorageState();

		if (mediaState.equals(Environment.MEDIA_MOUNTED)) {
			try {

				br = new BufferedReader(new FileReader(
						getExternalFilesDir(null).getPath()
								+ "/Locais_Favoritos_Mobilidade_Ext.txt"));

				while ((line = br.readLine()) != null) {
					conteudo = line;
					favoritos.add(conteudo);
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
		}
		return favoritos;
	}
}
