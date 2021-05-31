package com.example.repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Article;
import com.example.domain.Comment;

/**
 * articlesテーブルを処理するリポジトリ.
 *
 * @author shigeki.morishita
 *
 */
@Repository
public class ArticleRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	/** TABLE_NAME */
	private static final String TABLE_NAME = "articles";

	/** ARTICLEオブジェクトを生成するローマッパー */
//	private final static RowMapper<Article> ARTICLE_ROW_MAPPER = new BeanPropertyRowMapper<>(Article.class);

	/** ARTICLE_RESULT_SET_EXTRACTOR */
	private static final ResultSetExtractor<List<Article>> ARTICLE_EXTRACTOR = (rs) -> {
		List<Article> articleList = new LinkedList<Article>();
		List<Comment> commentList = null;
		int beforeArticleId = 0;
		while (rs.next()) {
			int nowArticleId = rs.getInt("id");
			if (nowArticleId != beforeArticleId) {
				Article article = new Article();
				article.setId(nowArticleId);
				article.setName(rs.getString("name"));
				article.setContent(rs.getString("content"));
				commentList = new ArrayList<Comment>();
				article.setCommentList(commentList);
				articleList.add(article);
			}
			if (rs.getInt("b_id") != 0) {
				Comment comment = new Comment();
				comment.setId(rs.getInt("b_id"));
				comment.setName(rs.getString("b_name"));
				comment.setContent(rs.getString("b_content"));
				commentList.add(comment);
			}
			beforeArticleId = nowArticleId;
		}
		return articleList;
	};

	/**
	 * 記事を全検索を取得する.
	 *
	 * @return List<Article> 全検索結果
	 */
//	public List<Article> findAll() {
//		String sql = "SELECT id,name,content FROM " + TABLE_NAME + " ORDER BY id DESC;";
//
//		List<Article> articleList = template.query(sql, ARTICLE_ROW_MAPPER);
//
//		return articleList;
//	}

	/**
	 * 記事を全検索を取得する.
	 *
	 * @return List<Article> 全検索結果
	 */
	public List<Article> findAll() {
		String sql = "SELECT a.id, a.name, a.content, b.id b_id, b.name b_name, b.content b_content,b.article_id b_article_id "
				+ "FROM articles AS a LEFT OUTER JOIN comments AS b ON a.id = b.article_id ORDER BY a.id DESC, b.id;";
		List<Article> articleList = jdbcTemplate.query(sql, ARTICLE_EXTRACTOR);

		return articleList;
	}

	/**
	 * 記事を投稿する.
	 *
	 * @param article 記事内容
	 */
	public void insert(Article article) {
		String sql = "INSERT INTO " + TABLE_NAME + " (name,content) VALUES (:name,:content);";
		SqlParameterSource param = new BeanPropertySqlParameterSource(article);
		template.update(sql, param);
	}

	/**
	 * 記事を消去する.
	 *
	 * @param id 記事のid
	 */
	public void deleteById(int id) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = :id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);
	}
}
