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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.adapters.PostAdapter;
import com.blogspot.abtallaldigital.databinding.FragmentHomeBinding;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.utils.Constants;
import com.blogspot.abtallaldigital.utils.RecyclerItemClickListener;
import com.blogspot.abtallaldigital.utils.Utils;
import com.blogspot.abtallaldigital.utils.WrapContentLinearLayoutManager;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PostViewModel postViewModel;
    public static final String TAG = "HomeFragment";
    private PostAdapter adapter;
    private List<Item> itemArrayList;
    private GridLayoutManager titleLayoutManager, gridLayoutManager;
    WrapContentLinearLayoutManager layoutManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.homeRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(requireContext(),
                binding.homeRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Item item = itemArrayList.get(position);

                if (Objects.requireNonNull
                        (Navigation.findNavController(requireView())
                                .getCurrentDestination()).getId() == R.id.nav_home) {
                    Navigation.findNavController(requireView())
                            .navigate(HomeFragmentDirections.actionNavHomeToDetailsFragment(item));
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.finalURL.setValue(Constants.getBaseUrl() + "?key=" + Constants.getKEY());
        itemArrayList = new ArrayList<>();
        adapter = new PostAdapter(getContext(), itemArrayList, this, postViewModel);

        layoutManager = new WrapContentLinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        titleLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);


//        binding.homeRecyclerView.setAdapter(adapter);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.homeRecyclerView.setVisibility(View.INVISIBLE);

        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);


        postViewModel.recyclerViewLayoutMT.observe(getViewLifecycleOwner(), layout -> {
            Log.w(TAG, "getSavedLayout: called");
            switch (layout) {
                case "cardLayout":
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.homeRecyclerView.setLayoutManager(layoutManager);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.setViewType(0);

                    break;
                case "cardMagazineLayout":
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.homeRecyclerView.setLayoutManager(layoutManager);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.setViewType(1);
                    break;
                case "titleLayout":
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.homeRecyclerView.setLayoutManager(titleLayoutManager);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.setViewType(2);
                    break;
                case "gridLayout":
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.homeRecyclerView.setLayoutManager(gridLayoutManager);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.setViewType(3);
            }
        });


        if (Utils.hasNetworkAccess(requireContext())) {

            postViewModel.getPosts();

            postViewModel.postListMutableLiveData.observe(getViewLifecycleOwner(), postList -> {
                itemArrayList.addAll(postList.getItems());
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.homeRecyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();

                Log.e(TAG, "ItemsArrayList :" + itemArrayList.get(0).getTitle());

            });


        } else {

            binding.shimmerLayout.setVisibility(View.VISIBLE);
//            binding.shimmerLayout.startShimmer();


            if (postViewModel.getAllItemsFromDataBase == null) {

                noInternetConnectionLayout();

            } else {
//                Log.e(TAG, "RoomDB Items size :" + itemsDatabase.itemDAO().getAlItems());

                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.GONE);
                binding.homeRecyclerView.setVisibility(View.VISIBLE);
                postViewModel.getAllItemsFromDataBase.observe(getViewLifecycleOwner(), items -> {
                    if (items.isEmpty()) {
                        noInternetConnectionLayout();
                    } else {
                        binding.loadMoreBtn.setVisibility(View.GONE);
                        itemArrayList.addAll(items);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        }

        postViewModel.errorCode.observe(getViewLifecycleOwner(), errorCode -> {
            if (errorCode == 400) {
                Snackbar.make(requireView(), R.string.lastPost, Snackbar.LENGTH_LONG).show();
            } else {
                binding.homeRecyclerView.setVisibility(View.INVISIBLE);
                binding.emptyView.setVisibility(View.VISIBLE);
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
                postViewModel.getPosts();
//                Log.w(TAG, "loadMoreBtn: " + dialog.isShowing());
            } else {
                postViewModel.isLoading.postValue(true);
                postViewModel.getAllItemsFromDataBase.getValue();
                postViewModel.isLoading.postValue(false);
            }

        });

        return binding.getRoot();

    }

    private void noInternetConnectionLayout() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.homeRecyclerView.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        itemArrayList.clear();
        binding = null;
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
                if (Utils.hasNetworkAccess(requireContext())) {
                    itemArrayList.clear();
                    postViewModel.getItemsBySearch(keyword);
                    adapter.notifyDataSetChanged();
                } else {
                    postViewModel.getItemsBySearchInDB(keyword);
                    postViewModel.getItemsBySearchMT.observe(getViewLifecycleOwner(), items ->
                            {
                                Log.d(TAG, "onQueryTextSubmit database called");
                                itemArrayList.clear();
                                itemArrayList.addAll(items);
                                adapter.notifyDataSetChanged();
                            }
                    );
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchView.setOnCloseListener(() -> {

            if (Utils.hasNetworkAccess(requireContext())) {
                Log.d(TAG, "setOnCloseListener: called");
                itemArrayList.clear();
                binding.emptyView.setVisibility(View.GONE);
                binding.homeRecyclerView.setVisibility(View.VISIBLE);
                postViewModel.getPosts();
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "setOnCloseListener: called");
                binding.emptyView.setVisibility(View.GONE);
                binding.homeRecyclerView.setVisibility(View.VISIBLE);
                postViewModel.getAllItemsFromDataBase.observe(getViewLifecycleOwner(), items ->
                        {
                            itemArrayList.addAll(items);
                            adapter.notifyDataSetChanged();
                        }
                );
            }
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
        try {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(requireContext());

            builder.setTitle(getString(R.string.choose_layout));

            String[] recyclerViewLayouts = getResources().getStringArray(R.array.RecyclerViewLayouts);
//        SharedPreferences.Editor editor = sharedPreferences.edit();


            builder.setItems(recyclerViewLayouts, (dialog, index) -> {
                switch (index) {
                    case 0: // Card List Layout
                        adapter.setViewType(0);
                        binding.homeRecyclerView.setLayoutManager(layoutManager);
                        binding.homeRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "cardLayout");
//                    editor.apply();
                        postViewModel.saveRecyclerViewLayout("cardLayout");

                        break;
                    case 1: // Cards Magazine Layout
                        adapter.setViewType(1);
                        binding.homeRecyclerView.setLayoutManager(layoutManager);
                        binding.homeRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "cardMagazineLayout");
//                    editor.apply();
                        postViewModel.saveRecyclerViewLayout("cardMagazineLayout");
                        break;
                    case 2: // PostTitle Layout
                        adapter.setViewType(2);
                        binding.homeRecyclerView.setLayoutManager(titleLayoutManager);
                        binding.homeRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "titleLayout");
//                    editor.apply();
                        postViewModel.saveRecyclerViewLayout("titleLayout");
                        break;
                    case 3: //Grid Layout
                        adapter.setViewType(3);
                        binding.homeRecyclerView.setLayoutManager(gridLayoutManager);
                        binding.homeRecyclerView.setAdapter(adapter);
//                    editor.putString("recyclerViewLayout", "gridLayout");
//                    editor.apply();
                        postViewModel.saveRecyclerViewLayout("gridLayout");
                        break;
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "changeAndSaveLayout: " + e.getMessage());
            Log.e(TAG, "changeAndSaveLayout: " + e.getCause());
        }
    }

}