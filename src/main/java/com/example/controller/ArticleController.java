package com.example.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Article;
import com.example.domain.Comment;
import com.example.form.ArticleForm;
import com.example.form.CommentForm;
import com.example.repository.ArticleRepository;
import com.example.repository.CommentRepository;

/**
 * 投稿機能を制御するコントローラ.
 *
 * @author shigeki.morishita
 *
 */
@Controller
@Transactional
@RequestMapping("/bbs")
public class ArticleController {
	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private CommentRepository commentRepository;

	@ModelAttribute
	public ArticleForm setUpForm() {
		return new ArticleForm();
	}

	@ModelAttribute
	public CommentForm setUpCommentForm() {
		return new CommentForm();
	}

	/**
	 * 記事一覧を表示する.
	 *
	 * @param model モデル
	 * @return ビューへフォワード
	 */
	@RequestMapping("")
	public String index(Model model) {
		List<Article> articleList = articleRepository.findAll();
		for (Article article : articleList) {
			Integer articleId = article.getId();
			List<Comment> commentList = commentRepository.findByArticleId(articleId);
			article.setCommentList(commentList);
		}

		model.addAttribute("articleList", articleList);

		return "article-page";
	}

	/**
	 * 記事を追加する.
	 *
	 * @param article 投稿記事
	 * @return index(model)
	 */
	@RequestMapping("/add-article")
	public String addArticle(@Validated ArticleForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return index(model);
		}

		Article article = new Article();
		BeanUtils.copyProperties(form, article);
		articleRepository.insert(article);

		return "redirect:/bbs";
	}

	/**
	 * コメントを追加する.
	 *
	 * @param form  コメントに対するリクエスト
	 * @param model モデル
	 * @return index(model)
	 */
	@RequestMapping("/add-comment")
	public String addComment(@Validated CommentForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return index(model);
		}

		Comment comment = new Comment();
		BeanUtils.copyProperties(form, comment);
		comment.setContent(form.getComment());

		int articleId = Integer.parseInt(form.getArticleId());
		comment.setArticleId(articleId);

		commentRepository.insert(comment);
		return "redirect:/bbs";
	}

	/**
	 * 記事とコメントを消去する.
	 *
	 * @param id    記事のid
	 * @param model モデル
	 * @return index(model)
	 */
	@RequestMapping("/remove")
	public String removeArticleAndComment(String id, Model model) {
		int articleId = Integer.parseInt(id);

		commentRepository.deleteByArticleId(articleId);
		articleRepository.deleteById(articleId);

		return "redirect:/bbs";

	}

}
