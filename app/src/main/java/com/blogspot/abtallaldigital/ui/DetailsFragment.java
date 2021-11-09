package com.blogspot.abtallaldigital.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
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
import androidx.appcompat.widget.ShareActionProvider;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.data.database.FavoritesEntity;
import com.blogspot.abtallaldigital.databinding.FragmentDetailsBinding;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.utils.CustomTabsHelper;
import com.blogspot.abtallaldigital.utils.PicassoImageGetter;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    private FragmentDetailsBinding binding;

    String url, title, content, youtubeThumbnailImageSrc, youTubeLink;
    int youtubeThumbnailImageSetVisibility;
    private PostViewModel postViewModel;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        Log.d(TAG, "onCreate checkSavedFavoritesItems: " + postFavoritesSavedId);


        postItem = DetailsFragmentArgs.fromBundle(getArguments()).getPostItem();


        final Document document = Jsoup.parse(postItem.getContent());
//                    final Elements elements = document.select("img");

        Element element = document.body();

        for (Element e : element.getElementsByClass
                ("YOUTUBE-iframe-video")) {
            youtubeThumbnailImageSrc = e.attr("data-thumbnail-src");
            youTubeLink = e.attr("src");
            Log.e("YouTube thumbnail", youtubeThumbnailImageSrc);
            Log.e("Youtube link", youTubeLink);
        }

        if (youtubeThumbnailImageSrc == null) {
            youtubeThumbnailImageSetVisibility = 8;
        }

        url = postItem.getUrl();
        title = postItem.getTitle();
        content = postItem.getContent();


        //        blogImage = (ImageView) findViewById(R.id.blogImage);

        setHasOptionsMenu(true);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull( ((MainActivity) requireActivity())
                .getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.titleTextView.setText(title);
        binding.blogContent.setMovementMethod(LinkMovementMethod.getInstance());


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.fab.bringToFront();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                //   Log.d("ScrollView","scrollX_"+scrollX+"_scrollY_"+scrollY+"_oldScrollX_"+oldScrollX+"_oldScrollY_"+oldScrollY);
                if (scrollY > 0 && binding.fab.isShown()) {
                    binding.fab.hide();
                } else if (scrollY < 22) {
                    binding.fab.show();

                }
            });
        } else {
            binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                int mScrollY = binding.scrollView.getScrollY();
                if (mScrollY > 0 && binding.fab.isShown()) {
                    binding.fab.hide();
                } else if (mScrollY < 22) {
                    binding.fab.show();
                }
            });
        }


        binding.fab.setOnClickListener(view2 -> {
            String shareContent = title + "\n" + url;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            try {
                startActivity(Intent.createChooser(shareIntent, title));
            } catch (Exception exception) {
                Log.e(TAG, "onCreate: " + exception);
            }
        });


        //       String imageSrc = getIntent().getStringExtra("blogImage");
        //       Glide.with(getApplicationContext()).load(imageSrc).into(blogImage);


        binding.youtubeThumbnailImage.setVisibility(youtubeThumbnailImageSetVisibility);
        binding.youtubeThumbnailImage.setAdjustViewBounds(true);
        Picasso.get().load(youtubeThumbnailImageSrc).into(binding.youtubeThumbnailImage);

        binding.youtubeThumbnailImage.setOnClickListener(view1 -> {
            Intent youTube = new Intent
                    (Intent.ACTION_VIEW, Uri.parse(youTubeLink));
            startActivity(youTube);
        });


        PicassoImageGetter imageGetter = new PicassoImageGetter(binding.blogContent, requireContext());
        Spannable html;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(content, imageGetter, null);
        }

        binding.blogContent.setText(html);


        binding.visitSite.setOnClickListener(view3 -> openCustomTab(requireContext(), Uri.parse(url)));
        binding.progressBar.setVisibility(View.GONE);
    }

    private Item postItem;
    private boolean postFavoritesSaved = false;
    private int postFavoritesSavedId = 0;
    private MenuItem menuItem;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_menu, menu);
        menuItem = menu.findItem(R.id.action_add_to_favorites);
        checkSavedFavoritesItems(menuItem);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        menuItem.setIcon(R.drawable.ic_favorite_border);
    }

    private void checkSavedFavoritesItems(MenuItem menuItem) {
        postViewModel.getAllFavorites().observe(this, favoritesEntity -> {
            try {
                for (FavoritesEntity savedPost: favoritesEntity) {
                    if (savedPost.getItem().getId().equals(postItem.getId())) {
                        menuItem.setIcon(R.drawable.ic_favorite);
                        postFavoritesSavedId = savedPost.getId();
                        Log.d(TAG, "checkSavedFavoritesItems: " + postFavoritesSavedId);
                        postFavoritesSaved = true;
                    }
                }
            } catch (Exception exception) {
                Log.e(TAG, "checkSavedFavoritesItems: " + exception.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.action_add_to_favorites && !postFavoritesSaved) {
            saveTogFavorites(menuItem);
        } else if (menuItem.getItemId() == R.id.action_add_to_favorites && postFavoritesSaved) {
            removePostFromFavorites(menuItem);
        } else if (menuItem.getItemId() == R.id.action_share) {
            ShareActionProvider shareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            shareActionProvider.setShareIntent(createShareIntent());
            return true;
        } else if (menuItem.getItemId() == R.id.copyTheLink) {
            ClipboardManager clipboardManager = (ClipboardManager)
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("link", url);
            assert clipboardManager != null;
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), getString(R.string.linkCopied), Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void saveTogFavorites(MenuItem menuItem) {
        FavoritesEntity favoritesEntity = new FavoritesEntity(0,
                postItem);

        postViewModel.insertFavorites(favoritesEntity);
        menuItem.setIcon(R.drawable.ic_favorite);
        Snackbar.make(binding.getRoot(), "Saved", Snackbar.LENGTH_LONG).show();
        postFavoritesSaved = true;
    }


    private void removePostFromFavorites(MenuItem menuItem) {
        FavoritesEntity favoritesEntity = new FavoritesEntity(postFavoritesSavedId,
                postItem);

        Log.d(TAG, "checkSavedFavoritesItems: " + postFavoritesSavedId);
        postViewModel.deleteFavoritePost(favoritesEntity);
        menuItem.setIcon(R.drawable.ic_favorite_border);
        Snackbar.make(binding.getRoot(),
                "Post deleted from favorites", Snackbar.LENGTH_LONG).show();
        postFavoritesSaved = false;
    }


    private Intent createShareIntent() {
        String shareContent = title + "\n" + url;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        // shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return shareIntent;
    }

    public static void openCustomTab(Context context,
                                     Uri uri) {
        // Here is a method that returns the chrome package name
        String packageName = CustomTabsHelper.getPackageNameToUse(context);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent mCustomTabsIntent;
        mCustomTabsIntent = builder
                .setShowTitle(true)
                .build();
        // builder.setToolbarColor(ContextCompat.getColor(appCompatActivity, R.color.colorPrimary));

        if (packageName != null) {
            mCustomTabsIntent.intent.setPackage(packageName);
        }
        mCustomTabsIntent.launchUrl(context, uri);

    }
}