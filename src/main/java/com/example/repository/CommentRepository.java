package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Comment;

/**
 * commentsテーブルを処理するリポジトリ.
 * 
 * @author shigeki.morishita
 *
 */
@Repository
public class CommentRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;
	/** テーブル名 */
	private static final String TABLE_NAME = "comments";
	/** commentsオブジェクトを生成するローマッパー */
	private final static RowMapper<Comment> COMMENTS_ROW_MAPPER = new BeanPropertyRowMapper<>(Comment.class);

	/**
	 * 記事の全コメントをidで降順に取得する.
	 *
	 * @param articleId articleテーブルのid
	 * @return commentList コメントを降順で取得したリスト
	 */
	public List<Comment> findByArticleId(Integer articleId) {
		String sql = "SELECT id,name,content,article_id FROM " + TABLE_NAME
				+ " WHERE article_id = :articleId ORDER BY id DESC;";

		SqlParameterSource param = new MapSqlParameterSource().addValue("articleId", articleId);
		List<Comment> commentList = template.query(sql, param, COMMENTS_ROW_MAPPER);
		return commentList;
	}

	/**
	 * コメントを投稿する.
	 *
	 * @param comment コメント
	 */
	public void insert(Comment comment) {
		String sql = "INSERT INTO " + TABLE_NAME + " (name,content,article_id) VALUES (:name,:content,:articleId);";
		SqlParameterSource param = new BeanPropertySqlParameterSource(comment);
		template.update(sql, param);
	}

	/**
	 * コメントの消去を行う.
	 *
	 * @param articleId commentsテーブルのarticle_id
	 */
	public void deleteByArticleId(int articleId) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE article_id = :articleId;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("articleId", articleId);
		template.update(sql, param);
	}
}
