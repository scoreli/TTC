package tk.scoreli.liveticker;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import tk.scoreli.liveticker.data.DatabasehandlerSpiele;
import tk.scoreli.liveticker.data.Mitglied;
import tk.scoreli.liveticker.data.Veranstaltung;
import tk.scoreli.liveticker.loginregister.AppConfig;
import tk.scoreli.liveticker.loginregister.AppController;
import tk.scoreli.liveticker.loginregister.SessionManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateSpielActivity extends Activity {
	private EditText txfSpielstandHeim, txfSpielstandGast, txfStatus;
	private Button btnaktualisieren, btnloeschen;
	DatabasehandlerSpiele db = new DatabasehandlerSpiele(this);
	private static final String TAG = NeuesSpielActivity.class.getSimpleName();
	private ProgressDialog pDialog;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatespiel);
		init();
		// Session manager
		session = new SessionManager(getApplicationContext());
		// Progress dialog
		pDialog = new ProgressDialog(this);
		btnaktualisieren.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				aktualisieren();

			}

		});
		btnloeschen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (session.isLoggedIn()) {
					loeschen();
				} else {
					Toast.makeText(getApplicationContext(),
							"Bitte einloggen um Veranstaltungen zu löschen",
							Toast.LENGTH_SHORT).show();
				}

			}

		});
	}

	private void init() {
		txfSpielstandHeim = (EditText) findViewById(R.id.txfHeimmannschaftAktualisiere);
		txfSpielstandGast = (EditText) findViewById(R.id.txfGastmannschaftAktualisiere);
		txfStatus = (EditText) findViewById(R.id.txfAktualisiereStatus);
		btnaktualisieren = (Button) findViewById(R.id.btnaktualisieren);
		btnloeschen = (Button) findViewById(R.id.btnloeschen);
	}

	private void loeschen() {
		/*
		 * Hier wird die Zahl(id) der Veranstaltung geholt
		 */
		long i = getIntent().getExtras().getLong(SpieleActivity.KEY);
		Veranstaltung updateveranstaltung = db.getVeranstaltung((int) i);
		db.deleteVeranstaltung(updateveranstaltung);
		Veranstaltungloeschen("" + updateveranstaltung.getId());
		finish();
	}

	private void aktualisieren() {
		/*
		 * Hier wird die Zahl(id) der Veranstaltung geholt
		 */
		long i = getIntent().getExtras().getLong(SpieleActivity.KEY);
		Veranstaltung updateveranstaltung = db.getVeranstaltung((int) i);
		String spielstandheim = txfSpielstandHeim.getText().toString();
		String spielstandgast = txfSpielstandGast.getText().toString();
		String spielstatus = txfStatus.getText().toString();

		if (TextUtils.isEmpty(spielstandheim) == false) {
			updateveranstaltung.setSpielstandHeim(Integer
					.parseInt(spielstandheim));
		}
		if (TextUtils.isEmpty(spielstandgast) == false) {
			updateveranstaltung.setSpielstandGast(Integer
					.parseInt(spielstandgast));
		}
		if (TextUtils.isEmpty(spielstatus) == false) {
			updateveranstaltung.setStatus(spielstatus);
		}

		try {
			int ka = db.updateVeranstaltung(updateveranstaltung);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_spiel, menu);
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

	private void Veranstaltungloeschen(final String veranstaltungs_id) {
		// Tag used to cancel the request
		String tag_string_req = "req_loescheVeranstaltung";

		pDialog.setMessage("Löschen ...");
		showDialog();

		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_VERANSTALTUNG, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG,
								"Veranstaltung Response: "
										+ response.toString());
						hideDialog();
						/*
						 * try {
						 * 
						 * /* Toast.makeText(getApplicationContext(),
						 * response.toString(), Toast.LENGTH_SHORT) .show();
						 */
						/*
						 * JSONObject jObj = new JSONObject(response); //
						 * boolean error = jObj.getBoolean("error"); boolean
						 * error = false; if (!error) { // User successfully
						 * stored in MySQL // Now store the user in sqlite /*
						 * db.deleteVeranstaltungen();
						 * 
						 * JSONArray uebergabe = jObj
						 * .getJSONArray(TAG_VeranstaltungenDesUsers); for (int
						 * i = 0; i < uebergabe.length(); i++) { JSONObject
						 * veranstaltung = uebergabe .getJSONObject(i); String
						 * idj = veranstaltung .getString("veranstaltung_id");
						 * String heimmannschaftj = veranstaltung
						 * .getString("heimmannschaft"); String gastmannschaftj
						 * = veranstaltung .getString("gastmannschaft"); String
						 * punkteHeimj = veranstaltung .getString("punkteHeim");
						 * String punkteGastj = veranstaltung
						 * .getString("punkteGast"); String statusj =
						 * veranstaltung .getString("status"); String sportartj
						 * = veranstaltung .getString("sportart"); String
						 * spielbeginnj = veranstaltung
						 * .getString("spielbeginn"); db.addVeranstaltung(new
						 * Veranstaltung(Long .parseLong(idj), sportartj,
						 * heimmannschaftj, gastmannschaftj,
						 * Integer.parseInt(punkteHeimj),
						 * Integer.parseInt(punkteGastj), spielbeginnj,
						 * statusj));
						 * 
						 * }
						 * 
						 * 
						 * Toast.makeText(getApplicationContext(),
						 * "Aktualisiert", Toast.LENGTH_SHORT) .show(); } else {
						 * 
						 * // Error occurred in registration. Get the error //
						 * message // String errorMsg = //
						 * jObj.getString("error_msg");
						 * 
						 * // Toast.makeText(getApplicationContext(), //
						 * errorMsg, Toast.LENGTH_LONG).show();
						 * 
						 * } } catch (JSONException e) { // JSON error
						 * e.printStackTrace(); }
						 */
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Registration Error: " + error.getMessage());

						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();

					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "loescheveranstaltung");
				params.put("veranstaltungs_id", veranstaltungs_id);
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}
