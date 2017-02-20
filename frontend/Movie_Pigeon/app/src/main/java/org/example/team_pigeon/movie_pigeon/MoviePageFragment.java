package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.PublicRating;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SHENGX on 2017/2/4.
 */

public class MoviePageFragment extends Fragment {
    private static final String TAG = "MoviePageFragment";
    private static final String POST_RATING = "post rating";
    private static final String BOOKMARK = "bookmark";
    private static final String UNBOOKMARK = "unbookmark";
    private TextView txt_country, txt_length, txt_director, txt_plot, txt_genres, txt_year, txt_actors, score_imdb, score_douban, score_trakt, text_score;
    private TableRow row_country, row_length, row_director, row_genres, row_year, row_actors;
    private LinearLayout linearLayout_ratings;
    private ImageView image_poster, image_imdb, image_douban, image_trakt;
    private Movie movie;
    private ArrayList<PublicRating> publicRatings;
    private Button btn_bookmark, btn_rate, btn_submit;
    private AlertDialog ratingDialog;
    private AlertDialog.Builder dialogBuilder;
    private View ratingView;
    private SeekBar ratingBar;
    private String scoreToBePost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_page, container, false);
        ratingView = inflater.inflate(R.layout.dialog_rating, null, false);
        loadViews(view);
        setContent(getArguments());
        initRatingDialog(ratingView);
        return view;
    }

    private void loadViews(View view) {
        txt_country = (TextView) view.findViewById(R.id.text_movie_page_country_content);
        txt_director = (TextView) view.findViewById(R.id.text_movie_page_director_content);
        txt_plot = (TextView) view.findViewById(R.id.text_movie_page_plot_content);
        txt_length = (TextView) view.findViewById(R.id.text_movie_page_length_content);
        txt_genres = (TextView) view.findViewById(R.id.text_movie_page_genres_content);
        txt_year = (TextView) view.findViewById(R.id.text_movie_page_year_content);
        txt_actors = (TextView) view.findViewById(R.id.text_movie_page_actors_content);
        score_douban = (TextView) view.findViewById(R.id.rating_douban);
        score_imdb = (TextView) view.findViewById(R.id.rating_imdb);
        score_trakt = (TextView) view.findViewById(R.id.rating_trakt);
        image_poster = (ImageView) view.findViewById(R.id.image_page_poster);
        image_imdb = (ImageView) view.findViewById(R.id.image_imdb);
        image_douban = (ImageView) view.findViewById(R.id.image_douban);
        image_trakt = (ImageView) view.findViewById(R.id.image_trakt);
        row_year = (TableRow) view.findViewById(R.id.row_year);
        row_country = (TableRow) view.findViewById(R.id.row_country);
        row_director = (TableRow) view.findViewById(R.id.row_director);
        row_length = (TableRow) view.findViewById(R.id.row_length);
        row_genres = (TableRow) view.findViewById(R.id.row_genres);
        row_actors = (TableRow) view.findViewById(R.id.row_actors);
        linearLayout_ratings = (LinearLayout) view.findViewById(R.id.linear_ratings);
        btn_bookmark = (Button) view.findViewById(R.id.button_bookmark);
        btn_rate = (Button) view.findViewById(R.id.button_rate);
    }

    private void setContent(Bundle argument) {
        movie = (Movie) argument.getSerializable("movie");
        if (movie != null) {

            //Set movie poster
            if (movie.getPosterURL() != null) {
                ImageLoader.getInstance().displayImage(movie.getPosterURL(), image_poster);
            } else {
                image_poster.setImageResource(R.mipmap.image_no_poster_found);
            }

            //Set plot
            if (movie.getPlot() != null) {
                txt_plot.setText(movie.getPlot());
            }

            //Set other details
            setTextOtherwiseSetGone(movie.getProductionYear(), row_year, txt_year);
            setTextOtherwiseSetGone(movie.getCountry(), row_country, txt_country);
            setTextOtherwiseSetGone(movie.getLength(), row_length, txt_length);
            setTextOtherwiseSetGone(movie.getDirector(), row_director, txt_director);
            setTextOtherwiseSetGone(movie.getGenres(), row_genres, txt_genres);
            setTextOtherwiseSetGone(movie.getActors(), row_actors, txt_actors);

            //Set ratings
            publicRatings = movie.getPublicRatingses();
            if (publicRatings == null) {
                linearLayout_ratings.setVisibility(View.GONE);
            } else {
                setRatingsOthersSetGone(publicRatings.get(0).getScore(), score_imdb, image_imdb);
                setRatingsOthersSetGone(publicRatings.get(1).getScore(), score_douban, image_douban);
                setRatingsOthersSetGone(publicRatings.get(2).getScore(), score_trakt, image_trakt);
            }

            //Set buttons
            if (!movie.getUserRating().isEmpty()) {
                btn_rate.setText("Edit Your Rating");
            }
            if (!movie.getUserBookmark().isEmpty()) {
                btn_bookmark.setText("Unbookmark");
            }
            btn_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyTask bookmarkTask = new MyTask();
                    if (btn_bookmark.getText().equals("Bookmark")) {
                        bookmarkTask.execute(BOOKMARK, movie.getMovieID());
                    }
                    if (btn_bookmark.getText().equals("Unbookmark")) {
                        bookmarkTask.execute(UNBOOKMARK, movie.getMovieID());
                    }
                }
            });

            btn_rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratingDialog.show();
                }
            });
        }

    }

    private void setTextOtherwiseSetGone(String item, TableRow row, TextView text) {
        if (item != null) {
            if (row.equals(row_length)) {
                item = item.concat(" Min");
            }
            text.setText(item);
        } else {
            row.setVisibility(View.GONE);
        }
    }

    private void setRatingsOthersSetGone(String rating, TextView score, ImageView icon) {
        if (rating != null) {
            if (rating.length() >= 5) {
                rating = rating.substring(0, 3);
            }
            score.setText(rating);
        } else {
            score.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
        }
    }

    private void toggleBookmarkButton() {
        if (btn_bookmark.getText().equals("Bookmark")) {
            btn_bookmark.setText("Unbookmark");
        }
        if (btn_bookmark.getText().equals("Unbookmark")) {
            btn_bookmark.setText("Bookmark");
        }
    }

    private void initRatingDialog(View view) {
        dialogBuilder = new AlertDialog.Builder(this.getActivity());
        dialogBuilder.setView(ratingView);
        dialogBuilder.setCancelable(true);
        ratingDialog = dialogBuilder.create();
        ratingBar = (SeekBar) view.findViewById(R.id.seekbar_rating);
        text_score = (TextView) view.findViewById(R.id.text_score);
        btn_submit = (Button) view.findViewById(R.id.button_submit);
        btn_submit.setEnabled(false);
        ratingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_score.setText(progress / 10 + "." + progress % 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                btn_submit.setEnabled(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                scoreToBePost = decimalFormat.format((double) seekBar.getProgress() / 10);
            }
        });
        btn_submit.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyTask postRatingTask = new MyTask();
                postRatingTask.execute(POST_RATING, movie.getMovieID(), scoreToBePost);
            }
        });
    }

    //Async thread to handle search request
    private class MyTask extends AsyncTask<String, Integer, Integer> {
        private int successfulStatus = -1;
        private final int SUCCESSFUL_UNBOOKMARK = 2;
        private final int SUCCESSFUL_RATE = 0;
        private final int SUCCESSFUL_BOOKMARK = 4;
        private final int NO_INTERNET = 1;
        private int status;
        private String rating;
        RequestHttpBuilderSingleton requestHttpBuilder = RequestHttpBuilderSingleton.getInstance();

        @Override
        protected Integer doInBackground(String... params) {
            Request request = null;
            switch (params[0]) {
                case (POST_RATING):
                    request = requestHttpBuilder.getPostRatingRequest(params[1], params[2]);
                    successfulStatus = SUCCESSFUL_RATE;
                    break;
                case (BOOKMARK):
                    request = requestHttpBuilder.getPostBookmarkRequest(params[1]);
                    successfulStatus = SUCCESSFUL_BOOKMARK;
                    break;
                case (UNBOOKMARK):
                    request = requestHttpBuilder.getPostUnbookmarkRequest(params[1]);
                    successfulStatus = SUCCESSFUL_UNBOOKMARK;
                    break;
            }
            try {
                OkHttpClient client = requestHttpBuilder.getClient();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "rating failed" + response);
                    throw new IOException("Unexpected code" + response);
                } else {
                    status = successfulStatus;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return NO_INTERNET;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "New rating request is initialised");
            return;
        }

        @Override
        protected void onPostExecute(Integer params) {
            switch (status) {
                case SUCCESSFUL_RATE:
                    Log.i(TAG, "Rating is completed");
                    Toast.makeText(getContext(), "Update rating successfully!", Toast.LENGTH_SHORT).show();
                    btn_rate.setText("Edit Your Rating");
                    break;

                case SUCCESSFUL_BOOKMARK:
                    Log.i(TAG, "Bookmark is completed");
                    Toast.makeText(getContext(), "Bookmark successfully!", Toast.LENGTH_SHORT).show();
                    toggleBookmarkButton();
                    break;

                case SUCCESSFUL_UNBOOKMARK:
                    Log.i(TAG, "Unbookmark is completed");
                    Toast.makeText(getContext(), "Unbookmark successfully!", Toast.LENGTH_SHORT).show();
                    toggleBookmarkButton();
                    break;

                case NO_INTERNET:
                    Toast.makeText(getContext(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
                    break;
            }
            ratingDialog.dismiss();

        }
    }
}

