package de.ttcbeuren.ttcbeurenhauptapp.loginregister;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import de.ttcbeuren.ttcbeurenhauptapp.MainActivityStartseite;
import de.ttcbeuren.ttcbeurenhauptapp.R;
import de.ttcbeuren.ttcbeurenhauptapp.internet.AppConfig;
import de.ttcbeuren.ttcbeurenhauptapp.internet.AppController;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

	// LogCat tag
	private static final String TAG = LoginActivity.class.getSimpleName();
	/**
	 * Folgende Attribute sind für die Anzeige des Prozesses und für die
	 * Session. Diese wird gestartet wenn sich ein Benutzer anmeldet.
	 */
	private ProgressDialog pDialog;
	private SessionManager session;
	// private SQLiteHandlerLogin dblogin;
	// Fehlt noch muss hinzugefügt werden.
	private DatabasehandlerUUID dbuuid;
	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;
	private Button btnLogout, mEmailSignInButton, btnaccountdelete,
			btnaccpasswordchange, btnzuregister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		init();
		// Session manager
		session = new SessionManager(getApplicationContext());
		// SqLite database handler
		// dblogin = new SQLiteHandlerLogin(getApplicationContext());

		 dbuuid = new DatabasehandlerUUID(getApplicationContext());

		btnLogout.setVisibility(View.GONE);
		btnaccountdelete.setVisibility(View.GONE);
		btnaccpasswordchange.setVisibility(View.GONE);
		/*
		 * Wenn benutzer schon eingeloggt ist. Dann erscheint nur der Logout
		 * Button. Sonst ist er unsichtbar.
		 */
		if (session.isLoggedIn()) {
			btnLogout.setVisibility(View.VISIBLE);
			btnaccountdelete.setVisibility(View.VISIBLE);
			btnaccpasswordchange.setVisibility(View.VISIBLE);
			mEmailView.setVisibility(View.GONE);
			mPasswordView.setVisibility(View.GONE);
			mEmailSignInButton.setVisibility(View.GONE);
			btnzuregister.setVisibility(View.GONE);
		}

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		}

		);
		btnzuregister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);

			}
		});
		btnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});
		btnaccountdelete.setOnClickListener(new OnClickListener() {
			/**
			 * http://android-er.blogspot.kr/2012/03/example-of-using-
			 * popupwindow.html
			 */
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(
						R.layout.bestaetigenpopup, null);
				final PopupWindow popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				Button btnDismiss = (Button) popupView
						.findViewById(R.id.btn_dismiss);
				btnDismiss.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}
				});
				Button btnaccept = (Button) popupView
						.findViewById(R.id.btn_accept);
				btnaccept.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {

						/**
						 * Hier kommt der Auszuführende Code rein
						 */

						popupWindow.dismiss();
					}
				});
				//popupWindow.showAsDropDown(btnaccountdelete, 50, -30);
				popupWindow.showAtLocation(btnaccountdelete, Gravity.CENTER, 50, 30);
			}
		});

	}

	private void init() {
		mEmailView = (AutoCompleteTextView) findViewById(R.id.emailregister);
		btnLogout = (Button) findViewById(R.id.btn_logout);
		btnaccountdelete = (Button) findViewById(R.id.btn_acc_loeschen);
		btnaccpasswordchange = (Button) findViewById(R.id.btn_passwortaendern);
		btnzuregister = (Button) findViewById(R.id.btn_zuregister);
		mEmailSignInButton = (Button) findViewById(R.id.btn_registrieren);
		mPasswordView = (EditText) findViewById(R.id.passwordregister);
		mLoginFormView = findViewById(R.id.register_form);
		mProgressView = findViewById(R.id.register_progress);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		/*
		 * Überprüfe ob das Passwortfeld ausgefüllt ist
		 */
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			/**
			 * Ruft den Loginprozess auf.
			 */
			checkLogin(email, password);

		}
	}

	private boolean isEmailValid(String email) {
		// TODO: Replace this with your own logic
		return email.contains("@") && email.contains(".");
	}

	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 4;
	}

	/**
	 * 
	 * Hierbei wird das Passwort und die Email mit der My SQL Datenbank
	 * verglichen. Dabei wird die Volley Libary Benutzt. Hierbei wird ein PHP
	 * Script auf dem Server aberufen. Das dann mittels JSON die Abfragen regelt
	 * und diese auch so codiert wieder zurückgibt.Dieses funktioniert mit
	 * sogenannten Tags. Nach dem Loginvorgang wird dann diese Activity beendet
	 * und die Main gestartet.
	 * 
	 * @param email
	 * @param password
	 */
	private void checkLogin(final String email, final String password) {
		// Tag used to cancel the request
		String tag_string_req = "req_login";

		pDialog.setMessage("Einloggen ...");
		showDialog();
		// Post geht hier irgendwie nicht
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							JSONObject user = jObj.getJSONObject("user");

							// Check for error node in json
							if (!error) {
								String uid = jObj.getString("uid");
								String id = jObj.getString("id");
								String admin = jObj.getString("admin");

								try {
									dbuuid.addBenutzer(new Benutzer(Integer.parseInt(id),uid,Integer.parseInt(admin)));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// user successfully logged in
								// Create login session
								session.setLogin(true);

								// Launch main activity
								Intent intent = new Intent(LoginActivity.this,
										MainActivityStartseite.class);
								startActivity(intent);
								finish();
							} else {
								// Error in login. Get the error message
								String errorMsg = jObj.getString("error_msg");
								Toast.makeText(getApplicationContext(),
										errorMsg, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// JSON error
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Login Error: " + error.getMessage());
						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "login");
				params.put("email", email);
				params.put("password", password);

				return params;
			}

		};

		// Adding request to request queue
		try {
			AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	private void logoutUser() {
		session.setLogin(false);
		 dbuuid.deleteUsers(); // dblogin.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(LoginActivity.this,
				MainActivityStartseite.class);
		startActivity(intent);
		finish();
	}

}
