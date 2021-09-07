package com.blogspot.abtallaldigital.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.abtallaldigital.R;
import com.blogspot.abtallaldigital.databinding.CardLayoutBinding;
import com.blogspot.abtallaldigital.databinding.CardMagazineBinding;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.ui.DetailsActivity;
import com.blogspot.abtallaldigital.ui.home.HomeFragment;
import com.blogspot.abtallaldigital.utils.MyImageview;
import com.blogspot.abtallaldigital.viewmodels.PostViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<com.blogspot.abtallaldigital.pojo.Item> items;

    private static final int CARD = 0;
    private static final int CARD_MAGAZINE = 1;
    private static final int TITLE = 2;
    private static final int GRID = 3;
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;
    public static final String TAG = "POST ADAPTER";

    private int viewType;
    public final Fragment fragment;
    public final com.blogspot.abtallaldigital.viewmodels.PostViewModel postViewModel;

    public PostAdapter(Context context,
                       List<com.blogspot.abtallaldigital.pojo.Item> items, Fragment fragment,
                       PostViewModel postViewModel) {
        this.context = context;
        this.items = items;
        this.fragment = fragment;
        this.postViewModel = postViewModel;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        notifyDataSetChanged();
    }

    public int getViewType() {
        return this.viewType;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (this.viewType == CARD) {
            final CardLayoutBinding cardLayoutBinding
                    = CardLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CardViewHolder(cardLayoutBinding);
        } else if (this.viewType == CARD_MAGAZINE) {
            final CardMagazineBinding cardMagazineBinding
                    = CardMagazineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CardMagazineViewHolder(cardMagazineBinding);
        } else if (this.viewType == TITLE) {
            if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP) {
                view = inflater.inflate(R.layout.title_layout_v15, parent, false);
            } else {
                view = inflater.inflate(R.layout.title_layout, parent, false);
            }
            return new TitleViewHolder(view);
        } else {
            if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP) {
                view = inflater.inflate(R.layout.grid_layout_v15, parent, false);
            } else {
                view = inflater.inflate(R.layout.grid_layout, parent, false);
            }
            return new GridViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemType = getViewType();
        com.blogspot.abtallaldigital.pojo.Item item = items.get(holder.getAdapterPosition());
        final Document document = Jsoup.parse(item.getContent());
        final Elements elements = document.select("img");
        Intent intent = new Intent(context, com.blogspot.abtallaldigital.ui.DetailsActivity.class);

//        Log.e("IMAGE", document.getAllElements().select("img").get(0).attr("src"));


        switch (itemType) {
            case CARD:
                if (holder instanceof CardViewHolder) {
                    ((CardViewHolder) holder).bind(item);
                }
                break;

            case CARD_MAGAZINE:
                if (holder instanceof CardMagazineViewHolder) {
                    CardMagazineViewHolder
                            cardMagazineViewHolder = (CardMagazineViewHolder) holder;
                    cardMagazineViewHolder.bind(item);

                }
                break;
            case TITLE:
                if (holder instanceof TitleViewHolder) {
                    TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                    titleViewHolder.postTitle.setText(item.getTitle());

                    Log.d("TITLE", "title layout called");


                    try {
                        Log.e("IMAGE", elements.get(0).attr("src"));
                        Glide.with(context).load(elements.get(0).attr("src"))
                                .transition(DrawableTransitionOptions.withCrossFade(600))
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.no_image)
                                .into(titleViewHolder.postImage);
                    } catch (IndexOutOfBoundsException e) {
                        titleViewHolder.postImage.setImageResource(R.drawable.no_image);
                        Log.e(TAG, e.toString());
                    }

                    if (position == getItemCount() - 1)
                        if (fragment instanceof com.blogspot.abtallaldigital.ui.home.HomeFragment) {
                            postViewModel.getPosts();
                        } else {
                            postViewModel.getPostListByLabel();
                        }

                    titleViewHolder.itemView.setOnClickListener(view -> {
                        intent.putExtra("url", item.getUrl());
                        intent.putExtra("title", item.getTitle());
                        intent.putExtra("content", item.getContent());
                        int youtubeThumbnailImageSetVisibility = 0;

                        Element element = document.body();

                        String youtubeThumbnailImageSrc = "";
                        String youTubeLink = "";
                        for (Element e : element.getElementsByClass
                                ("YOUTUBE-iframe-video")) {
                            youtubeThumbnailImageSrc = e.attr("data-thumbnail-src");
                            youTubeLink = e.attr("src");
                            Log.e("YouTube thumbnail", youtubeThumbnailImageSrc);
                            Log.e("Youtube link", youTubeLink);
                        }

                        if (youtubeThumbnailImageSrc.isEmpty()) {
                            youtubeThumbnailImageSetVisibility = 8;
                            intent.putExtra("youtubeThumbnailImageSetVisibility",
                                    youtubeThumbnailImageSetVisibility);
                        } else {
                            intent.putExtra("youtubeThumbnailImageSrc", youtubeThumbnailImageSrc);
                            intent.putExtra("youTubeLink", youTubeLink);
                        }

//             String imageSrc = elements.get(0).attr("src");
//             intent.putExtra("blogImage",imageSrc);

                        view.getContext().startActivity(intent);
                    });

                }
                break;
            case GRID:
                if (holder instanceof GridViewHolder) {
                    GridViewHolder gridViewHolder = (GridViewHolder) holder;
                    gridViewHolder.postTitle.setText(item.getTitle());


                    try {
                        Log.e("IMAGE", elements.get(0).attr("src"));
                        Glide.with(context).load(elements.get(0).attr("src"))
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

                    gridViewHolder.itemView.setOnClickListener(view -> {
                        intent.putExtra("url", item.getUrl());
                        intent.putExtra("title", item.getTitle());
                        intent.putExtra("content", item.getContent());
                        int youtubeThumbnailImageSetVisibility = 0;

                        Element element = document.body();

                        String youtubeThumbnailImageSrc = "";
                        String youTubeLink = "";
                        for (Element e : element.getElementsByClass
                                ("YOUTUBE-iframe-video")) {
                            youtubeThumbnailImageSrc = e.attr("data-thumbnail-src");
                            youTubeLink = e.attr("src");
                            Log.e("YouTube thumbnail", youtubeThumbnailImageSrc);
                            Log.e("Youtube link", youTubeLink);
                        }

                        if (youtubeThumbnailImageSrc.isEmpty()) {
                            youtubeThumbnailImageSetVisibility = 8;
                            intent.putExtra("youtubeThumbnailImageSetVisibility",
                                    youtubeThumbnailImageSetVisibility);
                        } else {
                            intent.putExtra("youtubeThumbnailImageSrc", youtubeThumbnailImageSrc);
                            intent.putExtra("youTubeLink", youTubeLink);
                        }

//             String imageSrc = elements.get(0).attr("src");
//             intent.putExtra("blogImage",imageSrc);

                        view.getContext().startActivity(intent);
                    });
                }
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        //        ImageView postImage;
//        TextView postTitle, postDescription, postDate;
        final CardLayoutBinding cardLayoutBinding;
        final Context context;

        private CardViewHolder(final CardLayoutBinding binding) {
            super(binding.getRoot());
            cardLayoutBinding = binding;
            context = cardLayoutBinding.getRoot().getContext();


//            postImage = itemView.findViewById(R.id.postImage);
//            postTitle = itemView.findViewById(R.id.postTitle);
//            postDescription = itemView.findViewById(R.id.postDescription);
//            postDate = itemView.findViewById(R.id.postDate);

        }

        private void bind(com.blogspot.abtallaldigital.pojo.Item item) {
            final Document document = Jsoup.parse(item.getContent());
            final Elements elements = document.select("img");

//        Log.e("IMAGE", document.getAllElements().select("img").get(0).attr("src"));

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat
                    ("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
            Intent intent = new Intent(cardLayoutBinding.getRoot().getContext(), com.blogspot.abtallaldigital.ui.DetailsActivity.class);

            cardLayoutBinding.postTitle.setText(item.getTitle());

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
                date = format.parse(item.getPublished());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            PrettyTime prettyTime = new PrettyTime();

            cardLayoutBinding.postDate.setText(prettyTime.format(date));

            cardLayoutBinding.getRoot().setOnClickListener(view -> {

                intent.putExtra("url", item.getUrl());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("content", item.getContent());
                int youtubeThumbnailImageSetVisibility = 0;

                Element element = document.body();

                String youtubeThumbnailImageSrc = "";
                String youTubeLink = "";
                for (Element e : element.getElementsByClass
                        ("YOUTUBE-iframe-video")) {
                    youtubeThumbnailImageSrc = e.attr("data-thumbnail-src");
                    youTubeLink = e.attr("src");
                    Log.e("YouTube thumbnail", youtubeThumbnailImageSrc);
                    Log.e("Youtube link", youTubeLink);
                }

                if (youtubeThumbnailImageSrc.isEmpty()) {
                    youtubeThumbnailImageSetVisibility = 8;
                    intent.putExtra("youtubeThumbnailImageSetVisibility",
                            youtubeThumbnailImageSetVisibility);
                } else {
                    intent.putExtra("youtubeThumbnailImageSrc", youtubeThumbnailImageSrc);
                    intent.putExtra("youTubeLink", youTubeLink);
                }

//             String imageSrc = elements.get(0).attr("src");
//             intent.putExtra("blogImage",imageSrc);

                cardLayoutBinding.getRoot().getContext().startActivity(intent);
            });
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

        private void bind(Item item) {
            final Document document = Jsoup.parse(item.getContent());
            final Elements elements = document.select("img");


            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat
                    ("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
            Intent intent = new Intent(cardMagazineBinding.getRoot().getContext(),
                    DetailsActivity.class);


//        Log.e("IMAGE", document.getAllElements().select("img").get(0).attr("src"));
            cardMagazineBinding.postTitle.setText(item.getTitle());


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
                date = format.parse(item.getPublished());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            PrettyTime prettyTime = new PrettyTime();

            cardMagazineBinding.postDate.setText(prettyTime.format(date));

            cardMagazineBinding.getRoot().setOnClickListener(view -> {
                intent.putExtra("url", item.getUrl());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("content", item.getContent());
                int youtubeThumbnailImagesetVisibility = 0;

                Element element = document.body();

                String youtubeThumbnailImageSrc = "";
                String youTubeLink = "";
                for (Element e : element.getElementsByClass
                        ("YOUTUBE-iframe-video")) {
                    youtubeThumbnailImageSrc = e.attr("data-thumbnail-src");
                    youTubeLink = e.attr("src");
                    Log.e("YouTube thumbnail", youtubeThumbnailImageSrc);
                    Log.e("Youtube link", youTubeLink);
                }

                if (youtubeThumbnailImageSrc.isEmpty()) {
                    youtubeThumbnailImagesetVisibility = 8;
                    intent.putExtra("youtubeThumbnailImagesetVisibility",
                            youtubeThumbnailImagesetVisibility);
                } else {
                    intent.putExtra("youtubeThumbnailImageSrc", youtubeThumbnailImageSrc);
                    intent.putExtra("youTubeLink", youTubeLink);
                }

//             String imageSrc = elements.get(0).attr("src");
//             intent.putExtra("blogImage",imageSrc);

                cardMagazineBinding.getRoot().getContext().startActivity(intent);
            });

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