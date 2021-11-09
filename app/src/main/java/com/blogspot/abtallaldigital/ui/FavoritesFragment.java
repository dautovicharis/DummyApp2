package com.blogspot.abtallaldigital.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.adapters.FavoritesPostAdapter;
import com.blogspot.abtallaldigital.databinding.FragmentFavoritesBinding;
import com.blogspot.abtallaldigital.utils.WrapContentLinearLayoutManager;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;


import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private com.blogspot.abtallaldigital.viewmodels.PostViewModel postViewModel;
    private static final String TAG = "FavoritesFragment";
    private FavoritesPostAdapter favoritesPostAdapter;
    private GridLayoutManager titleLayoutManager, gridLayoutManager;
    com.blogspot.abtallaldigital.utils.WrapContentLinearLayoutManager layoutManager;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.favoritesRecyclerView.setVisibility(View.INVISIBLE);


        postViewModel.getAllFavorites().observe(getViewLifecycleOwner(), favoritesPostList -> {

            if (favoritesPostList.isEmpty()) {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Empty", Toast.LENGTH_LONG).show();
                binding.favoritesRecyclerView.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            } else {

                favoritesPostAdapter.addData(favoritesPostList);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.favoritesRecyclerView.setVisibility(View.VISIBLE);
                favoritesPostAdapter.notifyDataSetChanged();
            }
        });


    }

    private void changeAndSaveLayout() {
        android.app.AlertDialog.Builder builder
                = new android.app.AlertDialog.Builder(getContext());

        builder.setTitle(getString(R.string.choose_layout));

        String[] recyclerViewLayouts = getResources().getStringArray(R.array.RecyclerViewLayouts);
//        SharedPreferences.Editor editor = sharedPreferences.edit();


        builder.setItems(recyclerViewLayouts, (dialog, index) -> {
            switch (index) {
                case 0: // Card List Layout
                    favoritesPostAdapter.setViewType(0);
                    binding.favoritesRecyclerView.setLayoutManager(layoutManager);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
//                    editor.putString("recyclerViewLayout", "cardLayout");
//                    editor.apply();

                    postViewModel.saveRecyclerViewLayout("cardLayout");

                    break;
                case 1: // Cards Magazine Layout
                    favoritesPostAdapter.setViewType(1);
                    binding.favoritesRecyclerView.setLayoutManager(layoutManager);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
//                    editor.putString("recyclerViewLayout", "cardMagazineLayout");
//                    editor.apply();
                    postViewModel.saveRecyclerViewLayout("cardMagazineLayout");
                    break;
                case 2: // PostTitle Layout
                    favoritesPostAdapter.setViewType(2);
                    binding.favoritesRecyclerView.setLayoutManager(titleLayoutManager);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
//                    editor.putString("recyclerViewLayout", "titleLayout");
//                    editor.apply();
                    postViewModel.saveRecyclerViewLayout("titleLayout");
                    break;
                case 3: //Grid Layout
                    favoritesPostAdapter.setViewType(3);
                    binding.favoritesRecyclerView.setLayoutManager(gridLayoutManager);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
//                    editor.putString("recyclerViewLayout", "gridLayout");
//                    editor.apply();
                    postViewModel.saveRecyclerViewLayout("gridLayout");

            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.favorites_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAllFavoritePosts) {
            postViewModel.deleteAllFavorites();
            Snackbar.make(requireView(), "All favorites posts deleted", Snackbar.LENGTH_SHORT)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.change_layout) {
            changeAndSaveLayout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        favoritesPostAdapter =
                new FavoritesPostAdapter(requireActivity(), this, postViewModel);

        layoutManager = new WrapContentLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager = new LinearLayoutManager(this);
        titleLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);

        postViewModel.recyclerViewLayoutMT.observe(getViewLifecycleOwner(), layout -> {
            Log.w(TAG, "getSavedLayout: called");
            switch (layout) {
                case "cardLayout":
                    binding.favoritesRecyclerView.setLayoutManager(layoutManager);
                    favoritesPostAdapter.setViewType(0);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
                    break;
                case "cardMagazineLayout":
                    binding.favoritesRecyclerView.setLayoutManager(layoutManager);
                    favoritesPostAdapter.setViewType(1);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
                    break;
                case "titleLayout":
                    binding.favoritesRecyclerView.setLayoutManager(titleLayoutManager);
                    favoritesPostAdapter.setViewType(2);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
                    break;
                case "gridLayout":
                    binding.favoritesRecyclerView.setLayoutManager(gridLayoutManager);
                    favoritesPostAdapter.setViewType(3);
                    binding.favoritesRecyclerView.setAdapter(favoritesPostAdapter);
                    break;
            }
        });

        return binding.getRoot();
    }

}