package com.example.fitapp.activities;

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
import com.example.fitapp.classes.ContentArticle;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    private List<ContentArticle> articles;
    private Context context;

    public ArticlesAdapter(List<ContentArticle> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ContentArticle article = articles.get(position);
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

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvSnippet;
        Button btnViewContent;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvCategory = itemView.findViewById(R.id.tvArticleCategory);
            tvSnippet = itemView.findViewById(R.id.tvArticleSnippet);
            btnViewContent = itemView.findViewById(R.id.btnViewContent);
        }
    }
}
