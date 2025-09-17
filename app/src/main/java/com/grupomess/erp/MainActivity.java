package com.grupomess.erp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.grupomess.erp.databinding.ActivityMainBinding;

/**
 * Actividad principal de la aplicación Grupo Mess ERP.
 * Gestiona la navegación entre los diferentes fragmentos mediante un Drawer y Navigation Component.
 * Configura la barra de herramientas, el menú lateral y la navegación superior.
 * <br>
 * Flujo principal:
 * - Inicializa el binding de la vista principal.
 * - Configura la barra de herramientas y el menú de navegación.
 * - Define los destinos principales de la navegación.
 * - Gestiona la navegación entre fragmentos y el menú de opciones.
 *
 * @author SOLTICSS
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Configuración de la barra de navegación superior.
     */
    private AppBarConfiguration mAppBarConfiguration;
    /**
     * Binding para acceder a los elementos de la UI principal.
     */
    private ActivityMainBinding binding;

    /**
     * Metodo de ciclo de vida que inicializa la actividad y la navegación.
     * @param savedInstanceState estado guardado de la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_folio, R.id.nav_slideshow)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

/*        FloatingActionButton fab = binding.getRoot().findViewById(R.id.addFolio);

        fab.setOnClickListener(v -> {
            navController.navigate(R.id.nav_folio);
        });*/

    }

    /**
     * Infla el menú de opciones en la barra de herramientas.
     * @param menu menú de opciones
     * @return true si el menú se creó correctamente
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Gestiona la navegación al presionar el botón de navegación superior.
     * @return true si la navegación fue exitosa
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Maneja la selección de elementos del menú de opciones.
     * @param item elemento del menú seleccionado
     * @return true si el elemento fue manejado correctamente
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Abrir pantalla para cambiar contraseña
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            // Lógica para cerrar sesión
            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Cierra la sesión del usuario actual.
     * Realiza las siguientes acciones:
     * - Limpia los datos de SharedPreferences
     * - Limpia la pila de actividades
     * - Redirige al usuario a la pantalla de login
     */
    private void cerrarSesion() {
        try {
            // Limpiar datos de sesión
            SharedPreferences prefs = getSharedPreferences("mis_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            this.getCacheDir().delete();

            // Regresar al LoginActivity limpiando la pila de actividades
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error al cerrar sesión: " + e.getMessage(), e);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }




}