package com.blogspot.abtallaldigital.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.data.database.FavoritesEntity;
import com.blogspot.abtallaldigital.databinding.CardLayoutBinding;
import com.blogspot.abtallaldigital.databinding.CardMagazineBinding;
import com.blogspot.abtallaldigital.ui.FavoritesFragmentDirections;
import com.blogspot.abtallaldigital.ui.HomeFragment;
import com.blogspot.abtallaldigital.utils.MyImageview;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FavoritesPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final FragmentActivity fragmentActivity;
    private List<FavoritesEntity> favoritesList;
    private View rootView;

    private static final int CARD = 0;
    private static final int CARD_MAGAZINE = 1;
    private static final int TITLE = 2;
    private static final int GRID = 3;
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;
    public static final String TAG = "POST ADAPTER";

    private int viewType;
    public final Fragment fragment;
    public final PostViewModel postViewModel;
    private ActionMode mActionMode;
    private boolean multiSelection = false;
    //    private int selectedPostPosition ;
    private final List<FavoritesEntity> selectedPosts = new ArrayList<>();
    private final List<RecyclerView.ViewHolder> myViewHolders = new ArrayList<>();

    public FavoritesPostAdapter(FragmentActivity fragmentActivity,
                                Fragment fragment,
                                PostViewModel postViewModel) {
        this.fragmentActivity = fragmentActivity;
        this.favoritesList = new ArrayList<>();
        this.fragment = fragment;
        this.postViewModel = postViewModel;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        notifyDataSetChanged();
    }

    public void addData(List<FavoritesEntity> data) {
        this.favoritesList = data;
    }

    public int getViewType() {
        return this.viewType;
    }

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mActionMode = actionMode;
            actionMode.getMenuInflater().inflate(R.menu.favorites_contextual_menu, menu);
            applyStatusBarColor(R.color.contextualStatusBarColor);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.delete_favorites_post) {
                for (FavoritesEntity favoritesEntity : selectedPosts) {
                    postViewModel.deleteFavoritePost(favoritesEntity);
                }
                Log.d(TAG, "onActionItemClicked: " + favoritesList.size());
                Log.d(TAG, "onActionItemClicked: " + getItemCount());
                showSnackBar(selectedPosts.size() + " post/s deleted");
                multiSelection = false;
                selectedPosts.clear();
                mActionMode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

            for (RecyclerView.ViewHolder holder : myViewHolders) {
                changePostStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor);
            }

            multiSelection = false;
            selectedPosts.clear();
            applyStatusBarColor(R.color.statusBarColor);
        }
    };

    private void showSnackBar(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    private void applyStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragmentActivity.getWindow().setStatusBarColor(ContextCompat.getColor(fragmentActivity, color));
        }
    }

    private void applySelection(RecyclerView.ViewHolder holder, FavoritesEntity currentSelectedPost) {

        if (selectedPosts.contains(currentSelectedPost)) {
            selectedPosts.remove(currentSelectedPost);
            changePostStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor);
        } else {
            selectedPosts.add(currentSelectedPost);
            changePostStyle(holder, R.color.cardBackgroundLightColor, R.color.primaryColor);
        }
        applyActionModeTitle();
    }

    private void changePostStyle(RecyclerView.ViewHolder holder, int backgroundColor, int strokeColor) {
        if (holder instanceof CardViewHolder) {
            ((CardViewHolder) holder).cardLayoutBinding.secondLinearLayout.setBackgroundColor(
                    ContextCompat.getColor(fragmentActivity.getApplicationContext(),
                            backgroundColor)
            );
            ((CardViewHolder) holder).cardLayoutBinding.cardView.setStrokeColor(
                    ContextCompat.getColor(fragmentActivity.getApplicationContext(),
                            strokeColor
                    ));
        }
    }

    private void applyActionModeTitle() {
        if (selectedPosts.size() == 0) {
            mActionMode.finish();
            multiSelection = false;
        } else if (selectedPosts.size() == 1) {
            mActionMode.setTitle(selectedPosts.size() + " item selected");
        } else {
            mActionMode.setTitle(selectedPosts.size() + " items selected");
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragmentActivity);
        View view;

        if (this.viewType == CARD) {
            final CardLayoutBinding cardLayoutBinding
                    = CardLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FavoritesPostAdapter.CardViewHolder(cardLayoutBinding);
        } else if (this.viewType == CARD_MAGAZINE) {
            final CardMagazineBinding cardMagazineBinding
                    = CardMagazineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FavoritesPostAdapter.CardMagazineViewHolder(cardMagazineBinding);
        } else if (this.viewType == TITLE) {
            if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP) {
                view = inflater.inflate(R.layout.title_layout_v15, parent, false);
            } else {
                view = inflater.inflate(R.layout.title_layout, parent, false);
            }
            return new FavoritesPostAdapter.TitleViewHolder(view);
        } else {
            if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP) {
                view = inflater.inflate(R.layout.grid_layout_v15, parent, false);
            } else {
                view = inflater.inflate(R.layout.grid_layout, parent, false);
            }
            return new FavoritesPostAdapter.GridViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        myViewHolders.add(holder);
        rootView = holder.itemView.getRootView();
//        selectedPostPosition = position;

        int itemType = getViewType();
        FavoritesEntity favoriteItem = favoritesList.get(position);
        final Document document = Jsoup.parse(favoriteItem.getItem().getContent());
        final Elements elements = document.select("img");

//        Log.e("IMAGE", document.getAllElements().select("img").بget(0).attr("src"));


        switch (itemType) {
            case CARD:
                if (holder instanceof FavoritesPostAdapter.CardViewHolder) {
                    ((FavoritesPostAdapter.CardViewHolder) holder).bind(favoriteItem);

                    ((CardViewHolder) holder).cardLayoutBinding.cardView.setOnClickListener(view -> {
                                if (multiSelection) {
                                    applySelection(holder, favoriteItem);
                                } else {
                                    mActionMode.finish();
                                    if (Objects.requireNonNull(Navigation.findNavController(
                                            view
                                    ).getCurrentDestination()).getId() == R.id.nav_favorites) {
                                        Navigation.findNavController(view)
                                                .navigate(FavoritesFragmentDirections
                                                        .actionFavoritesFragmentToDetailsFragment(favoriteItem.getItem()));
                                    }
                                }
                            }
                    );
                    ((CardViewHolder) holder).cardLayoutBinding.cardView.setOnLongClickListener(view -> {
                        if (!multiSelection) {
                            multiSelection = true;
                            fragmentActivity.startActionMode(mActionModeCallback);
                            applySelection(holder, favoriteItem);
                            return true;
                        } else {
                            applySelection(holder, favoriteItem);
                            return true;
                        }

                    });
                }

                break;

            case CARD_MAGAZINE:
                if (holder instanceof FavoritesPostAdapter.CardMagazineViewHolder) {
                    FavoritesPostAdapter.CardMagazineViewHolder
                            cardMagazineViewHolder = (FavoritesPostAdapter.CardMagazineViewHolder) holder;
                    cardMagazineViewHolder.bind(favoriteItem);

                }
                break;
            case TITLE:
                if (holder instanceof FavoritesPostAdapter.TitleViewHolder) {
                    FavoritesPostAdapter.TitleViewHolder titleViewHolder = (FavoritesPostAdapter.TitleViewHolder) holder;
                    titleViewHolder.postTitle.setText(favoriteItem.getItem().getTitle());

                    Log.d("TITLE", "title layout called");


                    try {
                        Log.e("IMAGE", elements.get(0).attr("src"));
                        Glide.with(fragmentActivity).load(elements.get(0).attr("src"))
                                .transition(DrawableTransitionOptions.withCrossFade(600))
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.no_image)
                                .into(titleViewHolder.postImage);
                    } catch (IndexOutOfBoundsException e) {
                        titleViewHolder.postImage.setImageResource(R.drawable.no_image);
                        Log.e(TAG, e.toString());
                    }

                    if (position == getItemCount() - 1)
                        if (fragment instanceof HomeFragment) {
                            postViewModel.getPosts();
                        } else {
                            postViewModel.getPostListByLabel();
                        }

                }
                break;
            case GRID:
                if (holder instanceof FavoritesPostAdapter.GridViewHolder) {
                    FavoritesPostAdapter.GridViewHolder gridViewHolder = (FavoritesPostAdapter.GridViewHolder) holder;
                    gridViewHolder.postTitle.setText(favoriteItem.getItem().getTitle());


                    try {
                        Log.e("IMAGE", elements.get(0).attr("src"));
                        Glide.with(fragmentActivity).load(elements.get(0).attr("src"))
                                .transition(DrawableTransitionOptions.withCrossFade(600))
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.no_image)
                                .into(gridViewHolder.postImage);
                    } catch (IndexOutOfBoundsException e) {
                        gridViewHolder.postImage.setImageResource(R.drawable.no_image);
                        Log.e(TAG, e.toString());
                    }

                    if (position == getItemCount() - 1)
                        if (fragment instanceof HomeFragment) {
                            postViewModel.getPosts();
                        } else {
                            postViewModel.getPostListByLabel();
                        }

                }
        }


    }


    @Override
    public int getItemCount() {
        return favoritesList.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        final CardLayoutBinding cardLayoutBinding;
        final Context context;

        private CardViewHolder(final CardLayoutBinding binding) {
            super(binding.getRoot());
            cardLayoutBinding = binding;
            context = cardLayoutBinding.getRoot().getContext();


        }

        private void bind(FavoritesEntity favoriteItem) {
            final Document document = Jsoup.parse(favoriteItem.getItem().getContent());
            final Elements elements = document.select("img");

//        Log.e("IMAGE", document.getAllElements().select("img").get(0).attr("src"));

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat
                    ("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

            cardLayoutBinding.postTitle.setText(favoriteItem.getItem().getTitle());

            try {
                Log.e("IMAGE", elements.get(0).attr("src"));
                Glide.with(context).load(elements.get(0).attr("src"))
                        .transition(DrawableTransitionOptions.withCrossFade(600))
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.no_image)
                        .into(cardLayoutBinding.postImage);
            } catch (IndexOutOfBoundsException e) {
                cardLayoutBinding.postImage.setImageResource(R.drawable.no_image);
                Log.e(TAG, e.toString());
            }


            cardLayoutBinding.postDescription.setText(document.text());
            try {
                date = format.parse(favoriteItem.getItem().getPublished());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            PrettyTime prettyTime = new PrettyTime();

            cardLayoutBinding.postDate.setText(prettyTime.format(date));

        }
    }

    public static class CardMagazineViewHolder extends RecyclerView.ViewHolder {

        final CardMagazineBinding cardMagazineBinding;
        final Context context;

        private CardMagazineViewHolder(final CardMagazineBinding binding) {
            super(binding.getRoot());
            cardMagazineBinding = binding;
            context = cardMagazineBinding.getRoot().getContext();


        }

        private void bind(FavoritesEntity favoriteItem) {
            final Document document = Jsoup.parse(favoriteItem.getItem().getContent());
            final Elements elements = document.select("img");


            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat
                    ("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());


//        Log.e("IMAGE", document.getAllElements().select("img").get(0).attr("src"));
            cardMagazineBinding.postTitle.setText(favoriteItem.getItem().getTitle());


            try {
                Log.e("IMAGE", elements.get(0).attr("src"));
                Glide.with(context).load(elements.get(0).attr("src"))
                        .transition(DrawableTransitionOptions.withCrossFade(600))
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.no_image)
                        .into(cardMagazineBinding.postImage);
            } catch (IndexOutOfBoundsException e) {
                cardMagazineBinding.postImage.setImageResource(R.drawable.no_image);
                Log.e(TAG, e.toString());
            }

            try {
                date = format.parse(favoriteItem.getItem().getPublished());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            PrettyTime prettyTime = new PrettyTime();

            cardMagazineBinding.postDate.setText(prettyTime.format(date));

        }
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle;
        com.blogspot.abtallaldigital.utils.MyImageview postImage;


        private TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }


    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle;
        MyImageview postImage;


        private GridViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }
}
