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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.MenuItemCompat;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.databinding.ActivityDetailsBinding;
import com.blogspot.abtallaldigital.utils.CustomTabsHelper;
import com.blogspot.abtallaldigital.utils.PicassoImageGetter;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private ActivityDetailsBinding binding;

    String url, title, content, youtubeThumbnailImageSrc, youTubeLink;
    int youtubeThumbnailImageSetVisibility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        youtubeThumbnailImageSrc = getIntent().getStringExtra("youtubeThumbnailImageSrc");
        youtubeThumbnailImageSetVisibility = getIntent().getIntExtra("youtubeThumbnailImageSetVisibility", 0);
        youTubeLink = getIntent().getStringExtra("youTubeLink");



//        blogImage = (ImageView) findViewById(R.id.blogImage);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.titleTextView.setText(title);
        binding.blogContent.setMovementMethod(LinkMovementMethod.getInstance());



        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
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


        binding.fab.setOnClickListener(view -> {
            String shareContent = title + "\n" + url;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            try {
                startActivity(Intent.createChooser(shareIntent, title));
            }catch (Exception exception){
                Log.e(TAG, "onCreate: "+exception );
            }
        });


        //       String imageSrc = getIntent().getStringExtra("blogImage");
        //       Glide.with(getApplicationContext()).load(imageSrc).into(blogImage);


        binding.youtubeThumbnailImage.setVisibility(youtubeThumbnailImageSetVisibility);
        binding.youtubeThumbnailImage.setAdjustViewBounds(true);
        Picasso.get().load(youtubeThumbnailImageSrc).into(binding.youtubeThumbnailImage);

        binding.youtubeThumbnailImage.setOnClickListener(view -> {
            Intent youTube = new Intent
                    (Intent.ACTION_VIEW, Uri.parse(youTubeLink));
            startActivity(youTube);
        });


        PicassoImageGetter imageGetter = new PicassoImageGetter(binding.blogContent, this);
        Spannable html;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(content, imageGetter, null);
        }

        binding.blogContent.setText(html);


        binding.visitSite.setOnClickListener(view -> openCustomTab(this,Uri.parse(url)));
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            ShareActionProvider shareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            shareActionProvider.setShareIntent(createShareIntent());
            return true;
        } else if (item.getItemId() == R.id.copyTheLink) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("link", url);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this,getString(R.string.linkCopied),Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

        if ( packageName != null ) {
            mCustomTabsIntent.intent.setPackage(packageName);
        }
        mCustomTabsIntent.launchUrl(context, uri);

    }

}
