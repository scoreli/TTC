package de.ttcbeuren.ttcbeurenhauptapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import de.ttcbeuren.ttcbeurenhauptapp.spiele.DatabasehandlerSpiele;
import de.ttcbeuren.ttcbeurenhauptapp.spiele.Spiel;

public class NeuesSpielActivity extends Activity {
	private CheckBox checkbeurenistheim, checkspielistentschieden;
	private Spinner spnSpielsystem, spnMannschaftsart, spnBeurenMannschaft,
			spnGegnerMannschaft, spnGegnermannschaftsnummer;
	private TimePicker tpSpielbeginn, tpSpielende;
	private NumberPicker npBeuren, npGegner;
	private Button btnerstellen;
	private DatePicker date_spieldatum;
	private TextView etxtspielende;
	private EditText txt_status;
	DatabasehandlerSpiele dbspiele;
	/**
	 * Noch durch Datenbank oder XML Datei ersetzen.
	 */
	public String[] Mannschaftsarten = { "Herren", "Damen", "Jugend U18" };
	public String[] Spielsysteme = { "6 Gewinnpunkte", "8 Gewinnpunkte",
			"9 Gewinnpunkte" };
	public String[] BeurenMannschaftsnummer = { "I", "II", "III", "IV", "V",
			"VI" };
	public String[] GegnerMannschaftsnummer = { "I", "II", "III", "IV", "V",
			"VI", "VII", "VIII", "IX", "X" };
	public String[] Vereine = { "TSV Aach-Linz", "SV Allensbach ",
			"TTC Beuren a.d. Aach", "RV Bittelbrunn",
			"TSV Dettingen-Wallhausen", "TTC Engen-Aach",
			"SPVGG. F.A.L. Frickingen", "TV Gaienhofen", "TTS Gottmadingen",
			"TUS Immenstaad", "TTC Stockach-Zizenhausen", "TTV Anselfingen",
			"TV Jestetten", "TTC GW Konstanz", "SV Litzelstetten",
			"SV Bohlingen 1949 e.V.", "TSV Mimmenhausen" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neues_spiel);
		init();
		dbspiele = new DatabasehandlerSpiele(this);

		/**
		 * Bitte noch durch eine Automatik ersetzen.
		 */
		checkspielistentschieden
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							etxtspielende.setVisibility(View.VISIBLE);
							tpSpielende.setVisibility(View.VISIBLE);

						} else {
							etxtspielende.setVisibility(View.GONE);
							tpSpielende.setVisibility(View.GONE);
						}

					}
				});
		npBeuren.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {

			}
		});
		npGegner.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				// TODO Auto-generated method stub

			}
		});
		btnerstellen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Spielerstellen();

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.neues_spiel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_gegnerlisteaktualisieren) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void Spielerstellen() {
		Spiel neuesSpiel = null;
		/**
		 * Zeitrechnung beginnt ab 1900 daher müssen diese Jahre abgezogen
		 * werden. Da SQl Date nur die Jahre die seit 1900 vergangen sind
		 * benötigt.
		 */
		String spieldatumstring = "" + date_spieldatum.getYear() + "-"
				+ date_spieldatum.getMonth() + "-"
				+ date_spieldatum.getDayOfMonth() + " "
				+ tpSpielbeginn.getCurrentHour() + ":"
				+ tpSpielbeginn.getCurrentMinute() + ":" + 30;

		// Date spieldatum = new Date(date_spieldatum.getYear() - (1900),
		// date_spieldatum.getMonth(), date_spieldatum.getDayOfMonth());
		// Time spielbegintime = new Time(tpSpielbeginn.getCurrentHour(),
		// tpSpielbeginn.getCurrentMinute(), 0);

		int spielendestunde = 0;
		int spielendeminute = 0;
		String PunkteHeim;
		String PunkteGast;
		String Heimverein;
		String Heimvereinnummer;
		String Gastverein;
		String Gastvereinnummer;
		String Spielsystem = spnSpielsystem.getSelectedItem().toString();
		String Mannschaftsart = spnMannschaftsart.getSelectedItem().toString();
		String status = txt_status.getText().toString();
		if (checkbeurenistheim.isChecked()) {
			PunkteHeim = "" + npBeuren.getValue();
			PunkteGast = "" + npGegner.getValue();
			Heimverein = "TTC Beuren a.d. Aach";
			Heimvereinnummer = spnBeurenMannschaft.getSelectedItem().toString();
			Gastverein = spnGegnerMannschaft.getSelectedItem().toString();
			Gastvereinnummer = spnGegnermannschaftsnummer.getSelectedItem()
					.toString();

		} else {
			PunkteHeim = "" + npGegner.getValue();
			PunkteGast = "" + npBeuren.getValue();
			Heimverein = spnGegnerMannschaft.getSelectedItem().toString();
			Heimvereinnummer = spnGegnermannschaftsnummer.getSelectedItem()
					.toString();
			Gastverein = "TTC Beuren a.d. Aach";
			Gastvereinnummer = spnBeurenMannschaft.getSelectedItem().toString();

		}
		if (checkspielistentschieden.isChecked()) {
			// Time spielendetime = new Time(tpSpielende.getCurrentHour(),
			// tpSpielende.getCurrentMinute(), 0);
			String spielendeString = "" + tpSpielende.getCurrentHour() + ":"
					+ tpSpielende.getCurrentMinute() + ":" + 30;
			neuesSpiel = new Spiel(Integer.parseInt(PunkteHeim),
					Integer.parseInt(PunkteGast), Spielsystem, Mannschaftsart,
					Heimverein, Heimvereinnummer, Gastverein, Gastvereinnummer,
					status, spieldatumstring, spielendeString, 1);
			dbspiele.addSpiel(neuesSpiel);

		} else {
			neuesSpiel = new Spiel(Integer.parseInt(PunkteHeim),
					Integer.parseInt(PunkteGast), Spielsystem, Mannschaftsart,
					Heimverein, Heimvereinnummer, Gastverein, Gastvereinnummer,
					status, spieldatumstring, 0);
			dbspiele.addSpiel(neuesSpiel);
		}
		Toast.makeText(getApplicationContext(), neuesSpiel.toString(),
				Toast.LENGTH_LONG).show();
	}

	private void init() {
		viewsfreferenz();
		txt_status = (EditText) findViewById(R.id.etxt_status);
		checkbeurenistheim = (CheckBox) findViewById(R.id.check_beurenistheim);
		// Erst wenn die Checkbox spielentschieden aktiviert wird soll spielende
		// angezeigt werden und der time picker dafür.
		checkspielistentschieden = (CheckBox) findViewById(R.id.check_Spielistentschieden);
		etxtspielende = (TextView) findViewById(R.id.textView_Spielentschieden);
		etxtspielende.setVisibility(View.GONE);
		spnSpielsystem = (Spinner) findViewById(R.id.spn_spielsystem);
		spnMannschaftsart = (Spinner) findViewById(R.id.spn_mannschaftart);
		spnBeurenMannschaft = (Spinner) findViewById(R.id.spn_beurenmannschaft);
		spnGegnerMannschaft = (Spinner) findViewById(R.id.spn_gegnermannschaft);
		spnGegnermannschaftsnummer = (Spinner) findViewById(R.id.spn_gegnermannschaftsnummer);
		tpSpielbeginn = (TimePicker) findViewById(R.id.time_Spielbeginn);
		tpSpielbeginn.setIs24HourView(true);
		tpSpielende = (TimePicker) findViewById(R.id.time_Spielende);
		tpSpielende.setIs24HourView(true);
		tpSpielende.setVisibility(View.GONE);
		npBeuren = (NumberPicker) findViewById(R.id.np_heim);
		npBeuren.setMinValue(0);
		npBeuren.setMaxValue(9);
		npBeuren.setWrapSelectorWheel(true);
		// Blockt die Änderung für den Benutzer
		npBeuren.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npGegner = (NumberPicker) findViewById(R.id.np_gast);
		npGegner.setMinValue(0);
		npGegner.setMaxValue(9);
		npGegner.setWrapSelectorWheel(true);
		// Blockt die Änderung für den Benutzer
		npGegner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		date_spieldatum = (DatePicker) findViewById(R.id.spieldatum);
		date_spieldatum.setCalendarViewShown(false);
		btnerstellen = (Button) findViewById(R.id.btn_erstellen);
		spinnerreferenzen();
	}

	private void spinnerreferenzen() {
		ArrayAdapter<String> Mannschaftsartenadapter = new ArrayAdapter<String>(
				NeuesSpielActivity.this,
				android.R.layout.simple_spinner_dropdown_item, Mannschaftsarten);
		spnMannschaftsart.setAdapter(Mannschaftsartenadapter);

		ArrayAdapter<String> Spielsystemadapter = new ArrayAdapter<String>(
				NeuesSpielActivity.this,
				android.R.layout.simple_spinner_dropdown_item, Spielsysteme);
		spnSpielsystem.setAdapter(Spielsystemadapter);
		ArrayAdapter<String> Beurenmannschaftsnummeradapter = new ArrayAdapter<String>(
				NeuesSpielActivity.this,
				android.R.layout.simple_spinner_dropdown_item,
				BeurenMannschaftsnummer);
		spnBeurenMannschaft.setAdapter(Beurenmannschaftsnummeradapter);
		ArrayAdapter<String> Gegnermannschaftsnummeradapter = new ArrayAdapter<String>(
				NeuesSpielActivity.this,
				android.R.layout.simple_spinner_dropdown_item,
				GegnerMannschaftsnummer);
		spnGegnermannschaftsnummer.setAdapter(Gegnermannschaftsnummeradapter);
		ArrayAdapter<String> Vereinadapter = new ArrayAdapter<String>(
				NeuesSpielActivity.this,
				android.R.layout.simple_spinner_dropdown_item, Vereine);
		spnGegnerMannschaft.setAdapter(Vereinadapter);
	}

	private void viewsfreferenz() {

	}
}