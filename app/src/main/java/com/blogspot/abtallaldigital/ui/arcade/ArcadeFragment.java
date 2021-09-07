package com.blogspot.abtallaldigital.ui.arcade;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.adapters.PostAdapter;
import com.blogspot.abtallaldigital.databinding.FragmentArcadeBinding;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.utils.Constants;
import com.blogspot.abtallaldigital.utils.Utils;
import com.blogspot.abtallaldigital.utils.WrapContentLinearLayoutManager;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@SuppressWarnings("ALL")
@AndroidEntryPoint
public class ArcadeFragment extends Fragment {

    private FragmentArcadeBinding binding;
    public static final String TAG = "ArcadeFragment";
    private PostViewModel postViewModel;
    private PostAdapter adapter;
    private List<Item> itemArrayList;

    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private GridLayoutManager titleLayoutManager, gridLayoutManager;
    WrapContentLinearLayoutManager layoutManager;
    private SharedPreferences sharedPreferences;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        postViewModel.postListMutableLiveData.setValue(null);
        postViewModel.token.setValue("");
        postViewModel.finalURL.setValue("");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentArcadeBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.label.setValue("Arcade");
        itemArrayList = new ArrayList<>();

        adapter = new PostAdapter(getContext(), itemArrayList, this, postViewModel);

        layoutManager = new WrapContentLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager = new LinearLayoutManager(this);
        titleLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);


        String layout = getSavedLayout();
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.arcadeRecyclerView.setVisibility(View.INVISIBLE);


        if (postViewModel.token.getValue() == null) {

            postViewModel.finalURL.setValue(Constants.getBaseUrlPostsByLabel()
                    + "posts/search?q=label:Arcade&key=" + Constants.getKEY());
        } else {
            postViewModel.finalURL.setValue(Constants.getBaseUrlPostsByLabel()
                    + "posts?labels=Arcade&pageToken="
                    + postViewModel.token.getValue()
                    + "&key=" + Constants.getKEY());
        }

        if (Utils.hasNetworkAccess(requireContext())) {
            postViewModel.getPostListByLabel();
        } else {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.arcadeRecyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);

        }


        postViewModel.postListMutableLiveData.observe(getViewLifecycleOwner(), postList -> {
            itemArrayList.addAll(postList.getItems());
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.arcadeRecyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        });

        postViewModel.errorCode.observe(getViewLifecycleOwner(), errorCode -> {
            if (errorCode == 400) {
                Snackbar.make(requireView(), "You reached the last post", Snackbar.LENGTH_LONG).show();
            } else {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.arcadeRecyclerView.setVisibility(View.INVISIBLE);
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });

        binding.arcadeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (layout.equals("cardLayout") || layout.equals("cardMagazineLayout")) {
                        currentItems = layoutManager.getChildCount();
                        totalItems = layoutManager.getItemCount();
                        scrollOutItems = layoutManager.findFirstVisibleItemPosition();
                    } else if (layout.equals("titleLayout")) {
                        currentItems = titleLayoutManager.getChildCount();
                        totalItems = titleLayoutManager.getItemCount();
                        scrollOutItems = titleLayoutManager.findFirstCompletelyVisibleItemPosition();
                    } else {
                        currentItems = gridLayoutManager.getChildCount();
                        totalItems = gridLayoutManager.getItemCount();
                        scrollOutItems = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                    }
                    if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                        isScrolling = false;
                        postViewModel.getPostListByLabel();
                        adapter.notifyDataSetChanged();


                    }
                }

            }
        });

        return binding.getRoot();
    }

    @NonNull
    private String getSavedLayout() {
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String layout = sharedPreferences.getString("recyclerViewLayout", "cardLayout");
        switch (layout) {
            case "cardLayout":
                binding.arcadeRecyclerView.setLayoutManager(layoutManager);
                adapter.setViewType(0);
                binding.arcadeRecyclerView.setAdapter(adapter);
                break;
            case "cardMagazineLayout":
                binding.arcadeRecyclerView.setLayoutManager(layoutManager);
                adapter.setViewType(1);
                binding.arcadeRecyclerView.setAdapter(adapter);
                break;
            case "titleLayout":
                binding.arcadeRecyclerView.setLayoutManager(titleLayoutManager);
                adapter.setViewType(2);
                binding.arcadeRecyclerView.setAdapter(adapter);
                break;
            case "gridLayout":
                binding.arcadeRecyclerView.setLayoutManager(gridLayoutManager);
                adapter.setViewType(3);
                binding.arcadeRecyclerView.setAdapter(adapter);

        }
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.searchForPosts));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                if (keyword.isEmpty()) {
                    Snackbar.make(requireView(), "please enter keyword to search", Snackbar.LENGTH_SHORT).show();
                }
                itemArrayList.clear();
                postViewModel.getItemsBySearch(keyword);
                adapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchView.setOnCloseListener(() -> {
            Log.d(TAG, "setOnCloseListener: called");
            itemArrayList.clear();
            binding.emptyView.setVisibility(View.GONE);
            binding.arcadeRecyclerView.setVisibility(View.VISIBLE);
            postViewModel.getPosts();
            adapter.notifyDataSetChanged();
            return false;
        });


        postViewModel.searchError.observe(getViewLifecycleOwner(), searchError -> {
            if (searchError) {
                Toast.makeText(requireContext(),
                        "There's no posts with this keyword", Toast.LENGTH_LONG).show();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.change_layout) {
            changeAndSaveLayout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeAndSaveLayout() {
        android.app.AlertDialog.Builder builder
                = new android.app.AlertDialog.Builder(getContext());

        builder.setTitle(getString(R.string.choose_layout));

        String[] recyclerViewLayouts = getResources().getStringArray(R.array.RecyclerViewLayouts);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        builder.setItems(recyclerViewLayouts, (dialog, index) -> {
            switch (index) {
                case 0: // Card List Layout
                    adapter.setViewType(0);
                    binding.arcadeRecyclerView.setLayoutManager(layoutManager);
                    binding.arcadeRecyclerView.setAdapter(adapter);
                    editor.putString("recyclerViewLayout", "cardLayout");
                    editor.apply();
                    break;
                case 1: // Cards Magazine Layout
                    adapter.setViewType(1);
                    binding.arcadeRecyclerView.setLayoutManager(layoutManager);
                    binding.arcadeRecyclerView.setAdapter(adapter);
                    editor.putString("recyclerViewLayout", "cardMagazineLayout");
                    editor.apply();
                    break;
                case 2: // PostTitle Layout
                    adapter.setViewType(2);
                    binding.arcadeRecyclerView.setLayoutManager(titleLayoutManager);
                    binding.arcadeRecyclerView.setAdapter(adapter);
                    editor.putString("recyclerViewLayout", "titleLayout");
                    editor.apply();
                    break;
                case 3: //Grid Layout
                    adapter.setViewType(3);
                    binding.arcadeRecyclerView.setLayoutManager(gridLayoutManager);
                    binding.arcadeRecyclerView.setAdapter(adapter);
                    editor.putString("recyclerViewLayout", "gridLayout");
                    editor.apply();

            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}