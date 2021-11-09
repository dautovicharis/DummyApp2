package com.blogspot.abtallaldigital.ui;

import android.app.SearchManager;
import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.adapters.PostAdapter;
import com.blogspot.abtallaldigital.databinding.SportsFragmentBinding;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.utils.Constants;
import com.blogspot.abtallaldigital.utils.Utils;
import com.blogspot.abtallaldigital.utils.WrapContentLinearLayoutManager;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.schedulers.Schedulers;


@SuppressWarnings("ALL")
@AndroidEntryPoint
public class SportsFragment extends Fragment {

    private SportsFragmentBinding binding;
    public static final String TAG = "SportsFragment";
    private com.blogspot.abtallaldigital.viewmodels.PostViewModel postViewModel;
    private com.blogspot.abtallaldigital.adapters.PostAdapter adapter;
    private List<Item> itemArrayList;

    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private GridLayoutManager titleLayoutManager, gridLayoutManager;
    WrapContentLinearLayoutManager layoutManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SportsFragmentBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.label.setValue("Sports");

        itemArrayList = new ArrayList<>();
        adapter = new PostAdapter(getContext(), itemArrayList, this, postViewModel);

        layoutManager = new WrapContentLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager = new LinearLayoutManager(this);
        titleLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);


        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.sportsRecyclerView.setVisibility(View.INVISIBLE);

        postViewModel.recyclerViewLayoutMT.observe(getViewLifecycleOwner(), layout -> {
            Log.w(TAG, "getSavedLayout: called");
            switch (layout) {
                case "cardLayout":
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.sportsRecyclerView.setLayoutManager(layoutManager);
                    adapter.setViewType(0);
                    binding.sportsRecyclerView.setAdapter(adapter);
                    break;
                case "cardMagazineLayout":
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.sportsRecyclerView.setLayoutManager(layoutManager);
                    adapter.setViewType(1);
                    binding.sportsRecyclerView.setAdapter(adapter);
                    break;
                case "titleLayout":
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.sportsRecyclerView.setLayoutManager(titleLayoutManager);
                    adapter.setViewType(2);
                    binding.sportsRecyclerView.setAdapter(adapter);
                    break;
                case "gridLayout":
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.sportsRecyclerView.setLayoutManager(gridLayoutManager);
                    adapter.setViewType(3);
                    binding.sportsRecyclerView.setAdapter(adapter);
                    break;
            }
        });

        if (postViewModel.token.getValue() == null) {

            postViewModel.finalURL.setValue(Constants.getBaseUrlPostsByLabel()
                    + "posts/search?q=label:Sports&key=" + Constants.getKEY());
        } else {
            postViewModel.finalURL.setValue(Constants.getBaseUrlPostsByLabel()
                    + "posts?labels=Sports&pageToken="
                    + postViewModel.token.getValue()
                    + "&key=" + Constants.getKEY());
        }


        if (Utils.hasNetworkAccess(requireContext())) {
            postViewModel.getPostListByLabel();
        } else {
            noInternetConnectionLayout();

        }

        postViewModel.postListMutableLiveData.observe(getViewLifecycleOwner(), postList -> {
            itemArrayList.addAll(postList.getItems());
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.sportsRecyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        });

        postViewModel.errorCode.observe(getViewLifecycleOwner(), errorCode -> {
            if (errorCode == 400) {
                Snackbar.make(requireView(), "You reached the last post", Snackbar.LENGTH_LONG).show();
            } else {
                noInternetConnectionLayout();
            }
        });

        binding.loadMoreBtn.setOnClickListener(view -> {
            AlertDialog dialog = Utils.setProgressDialog(requireContext());

            postViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
                if (isLoading) {
                    dialog.show();
                } else {
                    dialog.dismiss();
                }
            });

            if (Utils.hasNetworkAccess(requireContext())) {
                postViewModel.getPostListByLabel();
                Log.w(TAG, "loadMoreBtn: " + dialog.isShowing());
            } else {
                noInternetConnectionLayout();
            }

        });

        return binding.getRoot();

    }

    private void noInternetConnectionLayout() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.sportsRecyclerView.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
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
            binding.sportsRecyclerView.setVisibility(View.VISIBLE);
            postViewModel.getPostListByLabel();
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
        AlertDialog.Builder builder
                = new AlertDialog.Builder(requireContext());

        builder.setTitle(getString(R.string.choose_layout));

        String[] recyclerViewLayouts = getResources().getStringArray(R.array.RecyclerViewLayouts);
//        SharedPreferences.Editor editor = sharedPreferences.edit();


        builder.setItems(recyclerViewLayouts, (dialog, index) -> {
            switch (index) {
                case 0: // Card List Layout
                    adapter.setViewType(0);
                    binding.sportsRecyclerView.setLayoutManager(layoutManager);
                    binding.sportsRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "cardLayout");
//                    editor.apply();

                    postViewModel.saveRecyclerViewLayout("cardLayout");

                    break;
                case 1: // Cards Magazine Layout
                    adapter.setViewType(1);
                    binding.sportsRecyclerView.setLayoutManager(layoutManager);
                    binding.sportsRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "cardMagazineLayout");
//                    editor.apply();
                    postViewModel.saveRecyclerViewLayout("cardMagazineLayout");
                    break;
                case 2: // PostTitle Layout
                    adapter.setViewType(2);
                    binding.sportsRecyclerView.setLayoutManager(titleLayoutManager);
                    binding.sportsRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "titleLayout");
//                    editor.apply();
                    postViewModel.saveRecyclerViewLayout("titleLayout");
                    break;
                case 3: //Grid Layout
                    adapter.setViewType(3);
                    binding.sportsRecyclerView.setLayoutManager(gridLayoutManager);
                    binding.sportsRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "gridLayout");
//                    editor.apply();
                    postViewModel.saveRecyclerViewLayout("gridLayout");

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}