package com.grupomess.erp;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
     * Método de ciclo de vida que inicializa la actividad y la navegación.
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
}