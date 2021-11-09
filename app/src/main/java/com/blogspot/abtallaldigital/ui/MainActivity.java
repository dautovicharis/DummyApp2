package com.blogspot.abtallaldigital.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.databinding.ActivityMainBinding;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @SuppressWarnings("unused")
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    NavGraph navGraph;
    private PostViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);


        setSupportActionBar(binding.appBarMain.toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_accessory,
                R.id.nav_arcade, R.id.nav_fashion,
                R.id.nav_food, R.id.nav_heath,
                R.id.nav_lifestyle, R.id.nav_sports, R.id.nav_favorites, R.id.about)
                .setOpenableLayout(binding.drawerLayout)
                .build();


        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);


        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);

        postViewModel.currentDestination.observe(this, currentDestination -> {
            Log.w(TAG, "currentDestination: " + currentDestination);

            navGraph.setStartDestination(currentDestination);

            navController.getGraph().setStartDestination(currentDestination);
            navController.setGraph(navGraph);

        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d(TAG, "addOnDestinationChangedListener: " + destination.getId());
            postViewModel.saveCurrentDestination(destination.getId());
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}