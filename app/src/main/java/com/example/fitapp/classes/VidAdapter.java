package com.example.fitapp.classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.activities.WebViewActivity;

import java.util.List;

/**
 * RecyclerView Adapter for displaying articles and video items in the Knowledge section.
 * It manages a list of {@link videos} objects and binds them to the UI components.
 */
public class VidAdapter extends RecyclerView.Adapter<VidAdapter.ArticleViewHolder> {

    private final List<videos> articles;
    private final Context context;

    /**
     * Constructor for VidAdapter.
     *
     * @param articles List of video/article data.
     * @param context  The current context.
     */
    public VidAdapter(List<videos> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    /**
     * Inflates the item layout and creates a new {@link ArticleViewHolder}.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new ArticleViewHolder instance.
     */
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    /**
     * Binds the article data to the ViewHolder and sets up the click listener for viewing content.
     *
     * @param holder   The ViewHolder to update.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        videos article = articles.get(position);
        holder.tvTitle.setText(article.getTitle());
        
        StringBuilder categories = new StringBuilder();
        if (article.getCategory() != null) {
            for (String cat : article.getCategory().keySet()) {
                if (categories.length() > 0) categories.append(", ");
                categories.append(cat);
            }
        }
        holder.tvCategory.setText(categories.toString());
        holder.tvSnippet.setText(article.getBodyText());

        boolean hasVideo = article.getVideoUrl() != null && !article.getVideoUrl().isEmpty();
        
        holder.btnViewContent.setText(hasVideo ? R.string.watch_video : R.string.read_more);

        holder.btnViewContent.setOnClickListener(v -> {
            if (hasVideo) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", article.getVideoUrl());
                intent.putExtra("title", article.getTitle());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, R.string.article_detail_not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Returns the total number of items in the articles list.
     *
     * @return The number of items.
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * ViewHolder for an article/video item, holding references to the UI components.
     */
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        /** TextViews for article title, category, and snippet. */
        TextView tvTitle, tvCategory, tvSnippet;
        /** Button to view the content (video or more text). */
        Button btnViewContent;

        /**
         * Constructor for ArticleViewHolder.
         *
         * @param itemView The view of the article item.
         */
        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvCategory = itemView.findViewById(R.id.tvArticleCategory);
            tvSnippet = itemView.findViewById(R.id.tvArticleSnippet);
            btnViewContent = itemView.findViewById(R.id.btnViewContent);
        }
    }
}
