package cd.s;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cd.s.data.Ad;
import cd.s.data.Ad.AdItem;
import cd.s.data.AdInfo;
import cd.s.data.AddressInfo;
import cd.s.data.AppInfo;
import cd.s.data.Article;
import cd.s.data.ArticleInfo;
import cd.s.data.Block;
import cd.s.data.Product;
import cd.s.data.StockCoinsResult;
import cd.s.data.Column;
import cd.s.data.Comment;
import cd.s.data.CommentDelete;
import cd.s.data.CommentItem;
import cd.s.data.CommentLike;
import cd.s.data.DR;
import cd.s.data.FavoriteDelete;
import cd.s.data.Image;
import cd.s.data.IntegralInfo;
import cd.s.data.IntegralResult;
import cd.s.data.Media;
import cd.s.data.MsgNotification;
import cd.s.data.Order;
import cd.s.data.PhotoRecommand;
import cd.s.data.ResultMsg;
import cd.s.data.SearchHistory;
import cd.s.data.ResultMsgCode;
import cd.s.data.ServiceTime;
import cd.s.data.Translate;
import cd.s.data.Translate.TranslateExampleItem;
import cd.s.data.Translate.TranslateMeanItem;
import cd.s.data.Translate.TranslatePhraseItem;
import cd.s.data.Translate.TranslateSymbolsItem;
import cd.s.data.TranslateItem;
import cd.s.data.User;
import cd.s.data.AppInfo.Message;
import cd.util.Emulator;
import hz.dodo.Logger;
import hz.dodo.data.Empty;
import hz.dodo.data.JsonBase;

public class JsonParser extends JsonBase
{
	public static Article getArticle(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				Article art = new Article();
				
				art.sId = getString(json, "id");
				art.sTitle = getDealWithTitle(getString(json, "title"));
				art.sComment = getString(json, "comment");
				art.sUpdated = getString(json, "updated");
				art.sContent = getString(json, "content");
				art.sChannelId = getString(json, "channelId");
				art.sColumnId = getString(json, "columnId");
				art.sLanguage = getString(json, "language");
				art.sTag = getString(json, "tag");
				art.sAuthor = getString(json, "author");
				art.sUrl = getString(json, "url");
				art.sStoryType = getString(json, "storyType");
				art.iThumbnailStyle = getInt(json, "thumbnailStyle");
				art.iContentType = getInt(json, "contentType");
				art.iCanComment = getInt(json, "canComment");
				art.sPublishTime = getString(json, "publishTime");
				art.iWordCount = getInt(json, "wordCount");
				art.sSummary = getString(json, "summary");
				art.sColumnName = getString(json, "columnName");
				art.sSource = getString(json, "source");
				art.sChannelName = getString(json, "channelName");
				art.sJsonUrl = getString(json, "jsonUrl");
				art.sColumnDirname = getString(json, "columnDirname");
				art.sShareUrl = getString(json, "shareUrl");
				art.iImageCount = getInt(json, "imageCount");
				art.iCategories = getInt(json, "categories");
				art.sEditor = getString(json, "editor");
				art.sOriginUrl = getString(json, "originUrl");
				art.sSpecialId = getString(json, "specialId");
				art.sOriginSpecialId = getString(json, "originSpecialId");
				
				JSONObject jsonObject = s2Json(getString(json, "properties"));
				if(jsonObject != null)
				{
					art.sLinkTitle = getDealWithTitle(getString(jsonObject, "link_title"));
					art.sTemplateType = getString(jsonObject, "templateFileName");
					art.sLiveFlvUrl = getString(jsonObject, "flv_live_url");
				}
				
				art.setMedia(getMedia(getString(json, "mediaStream")));
				
				JSONArray jsonArray = s2Arr(getString(json, "thumbnails"));
				if(jsonArray != null && jsonArray.length() > 0)
				{
					int i1 = 0;
					while(i1 < jsonArray.length())
					{
						art.addThumbnail(getImage(jsonArray.getString(i1)));
						++i1;
					}
				}
				
				jsonArray = s2Arr(getString(json, "images"));
				if(jsonArray != null && jsonArray.length() > 0)
				{
					int i1 = 0;
					while(i1 < jsonArray.length())
					{
						art.addPicture(getImage(jsonArray.getString(i1)));
						++i1;
					}
				}
				
				return art;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getArticle() " + ext.toString());
		}
		return null;
	}
	
	private static String getDealWithTitle(String title)
	{
		try
		{
			if(!Empty.isEmpty(title))
			{
				title = title.replaceAll("<em>", "");
				title = title.replaceAll("<Em>", "");
				title = title.replaceAll("<eM>", "");
				title = title.replaceAll("<EM>", "");
				title = title.replaceAll("</em>", "");
				title = title.replaceAll("</Em>", "");
				title = title.replaceAll("</eM>", "");
				title = title.replaceAll("</EM>", "");
				title = title.replaceAll("</>", "");
				
				return title;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getDealWithTitle() " + ext.toString());
		}
		return null;
	}
	
	public static List<Article> getRelatedArtList(final String inJson)
	{
		try
		{
			ResultMsgCode msgCode = JsonParserUserCenter.getResultMsg(inJson);
			if(msgCode != null && msgCode.iStatus == 1)
			{
				JSONObject json = s2Json(inJson);
				
				if(json != null)
				{
					return getArtList(getString(json, "data"));
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("getRelatedArtList() " + ext.toString());
		}
		return null;
	}
	public static List<Article> getArtList(final String inJson)
	{
		try
		{
			JSONArray jArr = s2Arr(inJson);
			
			if(jArr != null && jArr.length() > 0)
			{
				List<Article> lt = new ArrayList<Article>(jArr.length());
				Article article;
				List<Image> thumbnails;
				int i1 = 0;
				while(i1 < jArr.length())
				{
					if(null != (article = getArticle(jArr.getString(i1))))
					{
						if(article.iThumbnailStyle != Article.THUMBNAILSTYPE_NOIMG)
						{
							article.iThumbnailStyle = Article.THUMBNAILSTYPE_NOIMG;
							
							if(!Empty.isEmpty(thumbnails = article.getThumbnails()))
							{
								Image image = null;
								String sProportion;
								int i2 = 0;
								while(i2 < thumbnails.size())
								{
									if(null != (image = thumbnails.get(i2)))
									{
										if(!Empty.isEmpty(sProportion = image.sProportion))
										{
											if((Image.IMG_PROPORTION_3_2).equals(sProportion))
											{
												article.iThumbnailStyle = Article.THUMBNAILSTYPE_NORMAL;
												break;
											}
										}
									}
									++ i2;
								}
							}
						}
						lt.add(article);
					}
					++i1;
				}
				return lt;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getArtList() " + ext.toString());
		}
		return null;
	}
	public static Image getImage(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				Image image = new Image();
				
				image.sId = getString(json, "id");
				image.sProportion = getString(json, "proportion");
				image.sUrl = getString(json, "url");
				image.sComment = getString(json, "comment");
				image.width = getInt(json, "width");
				image.height = getInt(json, "height");
				
				return image;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getImage() " + ext.toString());
		}
		
		return null;
	}
	public static Media getMedia(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				Media media = new Media();
				
				media.sType = getString(json, "type");
				media.sUrl = getString(json, "url");
				media.sOrientation = getString(json, "orientation");
				media.iDuration = getInt(json, "duration");
				
				return media;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getMedia() " + ext.toString());
		}
		
		return null;
	}
	public static PhotoRecommand getPhotoRecommand(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(json);
				if(resultMsg != null && resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS)
				{
					PhotoRecommand photoRecommand = new PhotoRecommand();
					photoRecommand.sArticleid = getString(json, "articleid");
					photoRecommand.sUid = getString(json, "uid");
					photoRecommand.ltArticle = getArtList(getString(json, "stories"));
					
					return photoRecommand;
				}
			}
			
		}
		catch(Exception ext)
		{
			Logger.e("getPhotoRecommand() " + ext.toString());
		}
		return null;
	}
	public static AppInfo getAppInfo(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));
				
				if(resultMsg != null && resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS)
				{
					AppInfo appInfo = new AppInfo();
					
					JSONObject json1 = s2Json(getString(json, "messages"));
					
					if(json1 != null)
					{
						Message msg = appInfo.new Message();
						
						msg.sCancelButtonName = getString(json1, "cancelButtonName");
						msg.sLink = getString(json1, "link");
						msg.sTitle = getString(json1, "title");
						msg.sContent = getString(json1, "content");
						msg.sLinkButtonName = getString(json1, "linkButtonName");
						
						appInfo.setMessage(msg);
					}
					
					json1 = s2Json(getString(json, "forceMessages"));
					
					if(json1 != null)
					{
						Message msg = appInfo.new Message();
						
						msg.sCancelButtonName = getString(json1, "cancelButtonName");
						msg.sLink = getString(json1, "link");
						msg.sTitle = getString(json1, "title");
						msg.sContent = getString(json1, "content");
						msg.sLinkButtonName = getString(json1, "linkButtonName");
						
						appInfo.setForceMessage(msg);
					}
					
					appInfo.iVersionCode = getInt(json, "versionCode");
					appInfo.iForceVersion = getInt(json, "forceVersion");
					appInfo.iAllCommentFlag = getInt(json, "allCommentFlag");
					appInfo.sPreloaded = getString(json, "preloaded");
					
					return appInfo;
				}
				
			}
		}
		catch(Exception ext)
		{
			Logger.e("getAppInfo() " + ext.toString());
		}
		
		return null;
	}
	
	
	public static ResultMsg getResultMsgIncludeUid(final String inJson, final String sUid)
	{
		try 
		{
			if(!Empty.isEmpty(sUid))
			{
				JSONObject json = s2Json(inJson);
				if(json != null)
				{
					String uid = getString(json, "uid");
					if(sUid.equals(uid))
					{
						return getResultMsg(inJson);
					}
				}
			}
		} 
		catch (Exception ext) 
		{
			Logger.e("getResultMsgIncludeUid():" + ext.toString());
		}
		return null;
	}
	
	public static ResultMsg getResultMsg(final JSONObject json)
	{
		try 
		{
			if(json != null)
			{
				if(json.has("msg__"))
				{
					String msg = getString(json, "msg__");
					
					if(!Empty.isEmpty(msg))
					{
						JSONObject json1 = s2Json(msg);
						
						if(json1 != null)
						{
							ResultMsg resultMsg = new ResultMsg();
							
							resultMsg.iMsgcode = getInt(json1, "code");
							resultMsg.sMsgdesc = getString(json1, "desc");
							
							return resultMsg;
						}
					}
				}
				else
				{
					ResultMsg resultMsg = new ResultMsg();
					
					resultMsg.iMsgcode = getInt(json, "code");
					resultMsg.sMsgdesc = getString(json, "desc");
					
					return resultMsg;
				}
			}
		} 
		catch (Exception ext) 
		{
			Logger.e("getResultMsg(): " + ext.toString());
		}
		return null;
	}
	
	public static ResultMsg getResultMsg(final String inJson)
	{
		return getResultMsg(s2Json(inJson));
	}
	
	public static Comment getComment(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));
				
				if(resultMsg != null)
				{
					Comment comment = new Comment();
					
					comment.iCommentFlag = getInt(json, "commentFlag");
					
					comment.sId = getString(json, "articleid");
					
					if(resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS)
					{
						comment.setUser(getUser(getString(json, "user")));
						
						comment.setCommentItemList(getCommentItemList(getString(json, "comments"), comment.sId));
					}
					else if(resultMsg.iMsgcode == ResultMsg.CODE_COMMENT_LIMITED)
					{
						comment.setResultMsg(resultMsg);
					}
				
					return comment;
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("getComment() " + ext.toString());
		}
		
		return null;
	}
	
	public static CommentDelete getCommentDelete(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));
				
				if(resultMsg != null && resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS)
				{
					CommentDelete commentDelete = new CommentDelete();
					
					commentDelete.iCommentFlag = getInt(json, "commentFlag");
					commentDelete.lCommentid = getLong(json, "commentid");
					commentDelete.sArticleid = getString(json, "articleid");
					commentDelete.sUserid = getString(json, "uid");
					commentDelete.iDeletedCnt = getInt(json, "deletedCnt");
				
					return commentDelete;
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("getCommentDelete() " + ext.toString());
		}
		
		return null;
	}
	
	public static CommentLike getCommentLike(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));
				
				if(resultMsg != null && (resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS || resultMsg.iMsgcode == ResultMsg.CODE_REPEAT_LIKE))
				{
					CommentLike commentLike = new CommentLike();
					
					commentLike.resultMsg = resultMsg;
					commentLike.sUid = getString(json, "uid");
					commentLike.sArticleid = getString(json, "articleid");
					commentLike.lCommentid = getLong(json, "commentid");
					
					return commentLike;
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("getCommentDelete() " + ext.toString());
		}
		
		return null;
	}
	
	public static List<CommentItem> getCommentItemList(final String inJson, final String articleId)
	{
		try
		{
			JSONArray jArr = s2Arr(inJson);
			
			if(jArr != null && jArr.length() > 0)
			{
				List<CommentItem> list = new ArrayList<>(jArr.length());
				
				CommentItem commentItem;
				int i1 = 0;
				while(i1 < jArr.length())
				{
					if((commentItem = getCommentItem(jArr.getString(i1), articleId)) != null)
					{
						list.add(commentItem);
					}
					++i1;
				}
				
				return list;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getCommentItem() " + ext.toString());
		}
		
		return null;
	}
	
	private static CommentItem getCommentItem(final String inJson, final String articleId)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				CommentItem commentItem = new CommentItem();
				
				commentItem.setUser(getUser(getString(json, "user")));
				commentItem.setArticle(getArticle(getString(json, "articleinfo")));
				
				commentItem.lCommentid = getLong(json, "commentid");
				commentItem.iLikesCnt = getInt(json, "likesCnt");
				commentItem.iLikesFlag = getInt(json, "likesFlag");
				commentItem.sComment = getString(json, "comment");
				commentItem.sCreatetime = getString(json, "createtime");
				commentItem.sArticleId = articleId;
				
				commentItem.setCommentItems(getCommentItemList(getString(json, "comments"), articleId));
				
				return commentItem;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getCommentItem() " + ext.toString());
		}
		
		return null;
	}
	
	public static ArticleInfo getArticleInfo(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ArticleInfo articleInfo = new ArticleInfo();
				
				articleInfo.iLikesFlag = getInt(json, "likesFlag");
				articleInfo.iFavoriteFlag = getInt(json, "favoriteFlag");
				articleInfo.iCommentsCnt = getInt(json, "commentsCnt");
				articleInfo.iLikesCnt = getInt(json, "likesCnt");
				articleInfo.iCommentFlag = getInt(json, "commentFlag");
				
				articleInfo.setCommentItemList(getCommentItemList(getString(json, "comments"), getString(json, "articleid")));
				return articleInfo;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getArticleInfo() " + ext.toString());
		}
		
		return null;
	}
	
	public static Translate getTranslate(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				Translate translate = new Translate();
				
				JSONObject jsonObject = s2Json(getString(json, "word_result"));
				if(jsonObject != null)
				{
					JSONObject jsonObject2 = s2Json(getString(jsonObject, "simple_means"));
					if(jsonObject2 != null)
					{
						translate.sWord = URLDecoder.decode(getString(jsonObject2, "word_name"), "UTF-8");
						
						JSONArray jsonArray = s2Arr(getString(jsonObject2, "symbols"));
						if(jsonArray != null && jsonArray.length() > 0)
						{
							int i1 = 0;
							while(i1 < jsonArray.length())
							{
								if((jsonObject2 = jsonArray.getJSONObject(i1)) != null)
								{
									translate.addTranslateSymbolsItem(getTranslateSymbolsItem(jsonObject2, translate));
								}
								i1 ++;
							}
						}
					}
					
					if(translate.iLauguage == Translate.LAUGUAGE_EN || translate.iLauguage == Translate.LAUGUAGE_ZH)
					{
						if(translate.iLauguage == Translate.LAUGUAGE_EN)
						{
							jsonObject2 = s2Json(getString(jsonObject, "cizuxiyu"));
							if(jsonObject2 != null)
							{
								JSONArray jsonArray = s2Arr(getString(jsonObject2, "cizu"));
								if(jsonArray != null && jsonArray.length() > 0)
								{
									JSONObject jsonObject3, jsonObject4;
									int i1 = 0;
									while(i1 < jsonArray.length())
									{
										if((jsonObject3 = s2Json(jsonArray.getString(i1))) != null)
										{
											TranslatePhraseItem phraseItem = translate.new TranslatePhraseItem();
											phraseItem.sName = getString(jsonObject3, "cizu_name");
											
											JSONArray array = s2Arr(getString(jsonObject3, "jx"));
											if(array != null && array.length() > 0)
											{
												int i2 = 0;
												while (i2 < array.length())
												{
													if((jsonObject4 = s2Json(array.getString(i2))) != null)
													{
														TranslateMeanItem item = translate.new TranslateMeanItem();
														item.sMeanEn = getString(jsonObject4, "jx_en_mean");
														item.sMeanCn = getString(jsonObject4, "jx_cn_mean");
														phraseItem.addTranslateMeanItem(item);
														
														JSONArray array2 = s2Arr(getString(jsonObject3, "lj"));
														if(array2 != null && array2.length() > 0)
														{
															int i3 = 0;
															while(i3 < array2.length())
															{
																if((jsonObject4 = s2Json(array2.getString(i3))) != null)
																{
																	TranslateExampleItem exampleItem = translate.new TranslateExampleItem();
																	exampleItem.sExampleEn = getString(jsonObject4, "lj_ly");
																	exampleItem.sExampleCn = getString(jsonObject4, "lj_ls");
																	item.addTranslateExampleItem(exampleItem);
																}
																i3 ++;
															}
														}
													}
													i2 ++;
												}
											}
											
											translate.addTranslatePhrase(phraseItem);
										}
										i1 ++;
									}
								}
							}
						}
						else
						{
							JSONArray jsonArray = s2Arr(getString(jsonObject, "cizuxiyu"));
							if(jsonArray != null && jsonArray.length() > 0)
							{
								JSONObject jsonObject3, jsonObject4;
								int i1 = 0;
								while(i1 < jsonArray.length())
								{
									if((jsonObject3 = s2Json(jsonArray.getString(i1))) != null)
									{
										TranslatePhraseItem phraseItem = translate.new TranslatePhraseItem();
										phraseItem.sName = getString(jsonObject3, "cz_name");
										
										JSONArray array = s2Arr(getString(jsonObject3, "jx"));
										if(array != null && array.length() > 0)
										{
											int i2 = 0;
											while (i2 < array.length())
											{
												if((jsonObject4 = s2Json(array.getString(i2))) != null)
												{
													TranslateMeanItem item = translate.new TranslateMeanItem();
													item.sMeanEn = getString(jsonObject4, "jx_cn");
													item.sMeanCn = getString(jsonObject4, "jx_en");
													phraseItem.addTranslateMeanItem(item);
												}
												i2 ++;
											}
										}
										
										translate.addTranslatePhrase(phraseItem);
									}
									i1 ++;
								}
							}
						}
						
						JSONArray array = s2Arr(getString(jsonObject, "dj"));
						if(array != null && array.length() > 0)
						{
							int i1 = 0;
							while(i1 < array.length())
							{
								if((jsonObject = s2Json(array.getString(i1))) != null)
								{
									TranslateExampleItem exampleItem = translate.new TranslateExampleItem();
									exampleItem.sExampleEn = getString(jsonObject, "Network_en");
									if(!Empty.isEmpty(exampleItem.sExampleEn))
									{
										exampleItem.sExampleEn = exampleItem.sExampleEn.replaceAll("</b>", "");
										exampleItem.sExampleEn = exampleItem.sExampleEn.replaceAll("<b>", "");
										exampleItem.sExampleEn = exampleItem.sExampleEn.replaceAll("</span>", "");
										String msg = "";
										int index = -1, index2 = -1;
										while ((index = exampleItem.sExampleEn.indexOf("<span")) != -1) 
										{
											if((index2 = exampleItem.sExampleEn.indexOf(">")) != -1)
											{
												if(index2 > index)
												{
													msg = exampleItem.sExampleEn.substring(index2 + 1);
													exampleItem.sExampleEn = exampleItem.sExampleEn.substring(0, index) + msg;
												}
											}
										}
									}
									exampleItem.sExampleCn = getString(jsonObject, "Network_cn");
									if(!Empty.isEmpty(exampleItem.sExampleCn))
									{
										exampleItem.sExampleCn = exampleItem.sExampleCn.replaceAll("</b>", "");
										exampleItem.sExampleCn = exampleItem.sExampleCn.replaceAll("<b>", "");
										exampleItem.sExampleCn = exampleItem.sExampleCn.replaceAll("</span>", "");
										String msg = "";
										int index = -1, index2 = -1;
										while ((index = exampleItem.sExampleCn.indexOf("<span")) != -1) 
										{
											if((index2 = exampleItem.sExampleCn.indexOf(">")) != -1)
											{
												if(index2 > index)
												{
													msg = exampleItem.sExampleCn.substring(index2 + 1);
													exampleItem.sExampleCn = exampleItem.sExampleCn.substring(0, index) + msg;
												}
											}
										}
									}
									translate.addTranslateExampleItem(exampleItem);
								}
								i1 ++;
							}
						}
					}
				}
				return translate;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getTranslate() " + ext.toString());
		}
		return null;
	}
	
	public static TranslateSymbolsItem getTranslateSymbolsItem(final JSONObject json, final Translate translate)
	{
		try
		{
			if(json != null && translate != null)
			{
				TranslateSymbolsItem symbolsItem = translate.new TranslateSymbolsItem();
				
				if(json.has(Translate.PREFIX_PH))
				{
					translate.iLauguage = Translate.LAUGUAGE_EN;
				}
				else if(json.has(Translate.PREFIX_SYMBOL))
				{
					translate.iLauguage = Translate.LAUGUAGE_ZH;
				}
				
				if(translate.iLauguage == Translate.LAUGUAGE_EN || translate.iLauguage == Translate.LAUGUAGE_ZH)
				{
					if(translate.iLauguage == Translate.LAUGUAGE_EN)
					{
						symbolsItem.setPronunciationEn(getString(json, "ph_en_mp3"));
						symbolsItem.setPronunciationAm(getString(json, "ph_am_mp3"));
						symbolsItem.sPhoneticEn = getString(json, "ph_en");
						symbolsItem.sPhoneticAm = getString(json, "ph_am");
					}
					else
					{
						symbolsItem.sPinYin = getString(json, "word_symbol");
					}
					
					JSONArray jsonArray = s2Arr(getString(json, "parts"));
					if(jsonArray != null && jsonArray.length() > 0)
					{
						TranslateItem translateItem;
						int i1 = 0;
						while(i1 < jsonArray.length())
						{
							if((translateItem = getTranslateItem(jsonArray.getString(i1), translate.iLauguage)) != null)
							{
								symbolsItem.addTranslateItem(translateItem);
							}
							i1 ++;
						}
					}
				}
				
				
				return symbolsItem;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getTranslateSymbolsItem() " + ext.toString());
		}
		
		return null;
	}
	
	public static TranslateItem getTranslateItem(final String inJson, final int lauguage)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				TranslateItem translate = new TranslateItem();
				
				if(lauguage == Translate.LAUGUAGE_EN)
				{
					translate.sPart = getString(json, "part");
				}
				else
				{
					String part = getString(json, "part_name");
					
					if(!Empty.isEmpty(part))
					{
						translate.sPart = part + " ";
					}
				}
				
				JSONArray array = s2Arr(getString(json, "means"));
				if(array != null && array.length() > 0)
				{
					JSONObject jsonObject;
					StringBuilder builder = new StringBuilder();
					int i1 = 0;
					while (i1 < array.length())
					{
						try
						{
							jsonObject = array.getJSONObject(i1);
							if(jsonObject != null)
							{
								String mean = getString(jsonObject, "word_mean");
								if(!Empty.isEmpty(mean))
								{
									builder.append(mean + "；");
								}
							}
						}
						catch (Exception e) 
						{
							builder.append(array.getString(i1) + "；");
						}
						i1 ++;
					}
					
					translate.sMeans = builder.toString();
				}
				
				return translate;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getTranslateItem() " + ext.toString());
		}
		
		return null;
	}
	
	public static ServiceTime getServiceTime(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));				
				if(resultMsg != null && resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS)
				{
					ServiceTime serviceTime = new ServiceTime();
					
					serviceTime.sServiceTime = getString(json, "servicetime");
					serviceTime.sPhoneTime = getString(json, "time");
					
					return serviceTime;
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("getServiceTime() " + ext.toString());
		}
		
		return null;
	}
	
	public static AdInfo getAdInfo(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				AdInfo adInfo = new AdInfo();
				
				adInfo.arcAd = getAd(getString(json, "arcAd"));
				adInfo.bsAd = getAd(getString(json, "bsAd"));
				
				return adInfo;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getAdInfo() " + ext.toString());
		}
		
		return null;
	}
	
	public static Ad getAd(final String inJson)
	{
		try
		{
			JSONArray jArr = s2Arr(inJson);
			
			if(jArr != null && jArr.length() > 0)
			{
				Ad ad = new Ad();
				
				JSONObject json;
				
				int i1 = 0;
				while(i1 < jArr.length())
				{
					if((json = s2Json(jArr.getString(i1))) != null)
					{
						AdItem adItem = ad.new AdItem();
						
						adItem.iId = getInt(json, "id");
						adItem.sTitle = getString(json, "title");
						adItem.sFileUrl = getString(json, "file");
						adItem.sActionUrl = getString(json, "adUrl");
						adItem.iMediaType = getInt(json, "mediaType");
						adItem.iWidth = getInt(json, "width");
						adItem.iHeight = getInt(json, "height");
						adItem.lDuration = getLong(json, "duration");
						
						ad.addAdItem(adItem);
					}
					++i1;
				}
				
				return ad;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getAd() " + ext.toString());
		}
		
		return null;
	}
	
	public static User getUser(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			
			if(json != null)
			{
				User user = new User();
				
				user.sId = getString(json, "uid");
				user.sNickname = getString(json, "nickname");
				user.sAvatarUrl = getString(json, "headPortraitUrl");
				user.iUserType = getInt(json, "userType");
				user.sLeveName = getString(json, "levelName");
				
				return user;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getUser() " + ext.toString());
		}
		
		return null;
	}
	
	public static List<Column> getColumns(final String inJson)
	{
		try
		{
			JSONArray jArr = s2Arr(inJson);

			if(jArr != null && jArr.length() > 0)
			{
				List<Column> lt = new ArrayList<>(jArr.length());

				Column column;

				int i1 = 0;
				while(i1 < jArr.length())
				{
					if((column = getColumn(jArr.getString(i1))) != null)
					{
						lt.add(column);
					}
					++i1;
				}

				return lt;
			}
		}catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}

	public static Column getColumn(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);

			if(json != null)
			{
				Column column = new Column();
				column.sUuid = (getString(json, "uuid"));
				column.sUpdated = (getString(json, "updated"));
				column.sName = (getString(json, "name"));
				column.sComment = (getString(json, "comment"));
				column.sParentId = (getString(json, "parentId"));
				column.iDisplayOrder = (getInt(json, "displayOrder"));
				//column.setChildren();忽略不计
				
				column.setChildren(getChildren(getString(json, "children")));
				
				column.sThumbnail3Url = (getString(json, "thumbnail3Url"));
				column.sThumbnail4Url = (getString(json, "thumbnail4Url"));
				column.sAlias = (getString(json, "alias"));
				if("VIDEO".equals(column.sAlias))
				{
					DataMng.VIDEO = column.sUuid;
				}
				else if("AUDIO".equals(column.sAlias))
				{
					DataMng.AUDIO = column.sUuid;
				}
				column.sSeoKeywords = (getString(json, "seoKeywords"));
				column.sSeoDescription = (getString(json, "seoDescription"));
				column.sThumbnail1Url = (getString(json, "thumbnail1Url"));
				column.sThumbnail2Url = (getString(json, "thumbnail2Url"));
				column.iPosition = (getInt(json, "position"));
				column.sDirname = (getString(json, "dirname"));
				column.sSeoTitle = (getString(json, "seoTitle"));
				column.sTreeOrder = (getString(json, "treeOrder"));
				column.iType = (getInt(json, "type"));

				return column;
			}
		}catch(Exception ext)
		{
			Logger.e("getColumn() " + ext.toString());
		}
		return null;
	}
	
	private static List<Article> getArtList(final String inJson, int style)
	{
		try
		{
			JSONArray jArr = s2Arr(inJson);

			if(jArr != null && jArr.length() > 0)
			{
				List<Article> lt = new ArrayList<Article>(jArr.length());

				Article article = null;

				int i1 = 0;
				while(i1 < jArr.length())
				{
					if((article = getArticle(jArr.getString(i1))) != null)
					{
						if(style != Block.BLOCK_NEW_ARTICLE)
						{
							//TODO article修改
							article.iThumbnailStyle = style;
							if(style == Block.BLOCK_SPECIL_COVER)
							{
								article.iBlockType = Article.BLOCKTYPE_SPECIAL;
							}
						}
						lt.add(article);
					}
					++i1;
				}

				return lt;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getArtList() " + ext.toString());
		}
		return null;
	}
	
	// 首页文章列表
	public static List<Block> getBlockArticles(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				return getBlocksList(getString(json, "blocks"));
			}
		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}

	//普通更多文章列表
	public static List<Article> getArticlesMore(final String inJson)
	{
		return getArtList(inJson);
	}

	// 普通文章列表
	public static List<Article> getArticles(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				List<Article> list = new ArrayList<>();

				List<Article> blocksList = getBlocksArtsList(getString(json, "blocks"));

				if(!Empty.isEmpty(blocksList))
				{
					list.addAll(blocksList);
				}

				List<Article> storiesList = getArtList(getString(json, "stories"));

				if(!Empty.isEmpty(storiesList))
				{
					list.addAll(storiesList);
				}
					
				return list;
			}
		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	
	//获取我的收藏
	public static List<Article> getArtFavorites(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));
				if(resultMsg != null && resultMsg.iMsgcode == ResultMsg.CODE_SUCCESS)
				{
					JSONArray jArr = s2Arr(getString(json, "favorites"));
					if(jArr != null && jArr.length() > 0)
					{
						List<Article> ltArts = new ArrayList<Article>(jArr.length());
						Article article = null;	
						int i1 = 0;
						while(i1 < jArr.length())
						{
							JSONObject json2 = s2Json(jArr.getString(i1));
							if(json2 != null)
							{
								if((article = getArticle(getString(json2, "articleinfo"))) != null)
								{
									article.lFavoriteid = getLong(json2, "favoriteid");
									ltArts.add(article);
								}
							}
							++i1;
						}
						return ltArts;
					}
				}
			}
		} 
		catch (Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	
	//意见反馈
		public static ResultMsg getFeedbackResultMsg(final String inJson)
		{
			try 
			{
				JSONObject json = s2Json(inJson);
				if(json != null)
				{
					ResultMsg resultMsg = null;
					if((resultMsg = getResultMsg(getString(json, "msg__"))) != null)
					{						
						return resultMsg;
					}
				}
			} 
			catch (Exception ext)
			{
				ext.printStackTrace();
			}
			return null;
		}

	
	//删除收藏
	public static FavoriteDelete getFavoriteDeletes(final String inJson, String sUid)
	{
		try 
		{
			if(!Empty.isEmpty(sUid))
			{
				JSONObject json = s2Json(inJson);
				if(json != null)
				{
					ResultMsg resultMsg = null;
					if((resultMsg = getResultMsg(getString(json, "msg__"))) != null)
					{
						FavoriteDelete favoriteDelete = new FavoriteDelete();
						favoriteDelete.sUid = getString(json, "uid");
						if(sUid.equals(favoriteDelete.sUid))
						{
							favoriteDelete.setResultMsg(resultMsg);
							favoriteDelete.setFavoriteDeleteIds(getFavoriteIds(getString(json, "deletedArticeids")));
							favoriteDelete.setFavoriteUndeleteIds(getFavoriteIds(getString(json, "undeletedArticeids")));
						}
						return favoriteDelete;
					}
				}
			}
		} 
		catch (Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}

	private static List<String> getFavoriteIds(String inJson)
	{
		try
		{
			JSONArray jArr = s2Arr(inJson);
			if(jArr != null && jArr.length() > 0)
			{
				String sArticleId = null;
				List<String> ltArticleIds = new ArrayList<>();
				int i1 = 0;
				while(i1 < jArr.length())
				{
					if(!Empty.isEmpty(sArticleId = jArr.getString(i1)));
					{
						ltArticleIds.add(sArticleId);
					}		
					++i1;
				}
				return ltArticleIds;
			}
		} 
		catch (Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	
	public static Block getBlock(final String inJson)
	{
		if(!Empty.isEmpty(inJson))
		{
			try
			{
				JSONObject json = s2Json(inJson);
				if(json != null)
				{

					Block blockArticle = new Block();
					blockArticle.sUuid = (getString(json, "uuid"));
					blockArticle.sName = (getString(json, "name"));
					blockArticle.iOrderTag = (getInt(json, "orderTag"));
					//分值position和style
					int orderTag = getInt(json, "orderTag");
					
					if (Math.abs(orderTag) > 999 && Math.abs(orderTag) < 10000)
					{
					    // 4位整数,前两位为排序，后两位为样式，保证为四位
						//(n & ( 1 << k )) >> k 一个n位数的第k位
						//blockArticle.iContentStyle = (orderTag & (1 << 1))>>1 + ((orderTag & (1 << 2))>>2)*10;
						//blockArticle.iPosition = (orderTag & (1 << 3))>>3 + ((orderTag & (1 << 4))>>4)*10;
						//blockArticle.iContentStyle = (orderTag >> 0)&1+((orderTag >> 1)&1)*10;
						//blockArticle.iPosition = (orderTag >> 2)&1+((orderTag >> 3)&1)*10;
					        int g = orderTag % 10;
					        int sw = orderTag / 10 % 10;
					        int b = orderTag / 100 % 10;
					        int q = orderTag / 1000 % 10;
					        blockArticle.iContentStyle = g + sw * 10;
					        blockArticle.iPosition = b + q * 10;
					}
					else
					{
						//赋初始值
						blockArticle.iOrderTag = -1;
						blockArticle.iContentStyle = -1;
					}

					//TODO 替换成article	
					List<Article> ltArt = getArtList(getString(json, "stories"), blockArticle.iContentStyle);
					if(ltArt != null)
					{
						blockArticle.setStories(ltArt);					
						return blockArticle;
					}
				}
			}
			catch(Exception ext)
			{
				ext.printStackTrace();
			}
		}
		return null;
	}
	
	public static List<Block> getBlocksList(final String inJson)
	{
		try
		{
			JSONArray jArrBlocks = s2Arr(inJson);

			if(jArrBlocks != null && jArrBlocks.length() > 0)
			{
				List<Block> lt = new ArrayList<>(jArrBlocks.length());
				Block block;
				int i1 = 0;
				while(i1 < jArrBlocks.length())
				{
					if((block = getBlock(jArrBlocks.getString(i1))) != null)
					{
						lt.add(block);
					}

					++i1;
				}
				
				return lt;
			}

		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	
	public static List<Column> getChildren(final String inJson)
	{
		try 
		{
			JSONArray jArrBlocks = s2Arr(inJson);
			if(jArrBlocks != null && jArrBlocks.length() > 0)
			{
				List<Column> lt = new ArrayList<>(jArrBlocks.length());
				Column column;
				int i1 = 0;
				while(i1 < jArrBlocks.length())
				{
					if((column = getColumn(jArrBlocks.getString(i1))) != null)
					{
						lt.add(column);
					}
					++i1;
				}
				return lt;
			}
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Article> getBlocksArtsList(final String inJson)
	{
		try
		{
			JSONArray jArrBlocks = s2Arr(inJson);

			if(jArrBlocks != null && jArrBlocks.length() > 0)
			{
				List<Article> lt = new ArrayList<>(jArrBlocks.length());
				Block block;
				int i1 = 0;
				while(i1 < jArrBlocks.length())
				{
					if((block = getBlock(jArrBlocks.getString(i1))) != null)
					{
						List<Article> stories = block.getStories();
						if(!Empty.isEmpty(stories))
						{
							lt.addAll(stories);
						}
					}
					++i1;
				}
				return lt;
			}

		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}

	//TODO 搜索历史记录相关
	public static String putSearchHistory(final List<SearchHistory> list)
	{
		try
		{
			if(Empty.isEmpty(list)) return null;

			JSONObject json;

			JSONArray jsonArray = new JSONArray();

			SearchHistory searchHistory;

			int i1 = 0;
			while(i1 < list.size())
			{
				if((searchHistory = list.get(i1)) != null && !Empty.isEmpty(searchHistory.msg))
				{
					json = new JSONObject();

					putString(json, "msg", searchHistory.msg);
					putLong(json, "time", searchHistory.time);
					putInt(json, "count", searchHistory.count);
					putLong(json, "id", searchHistory.id);

					jsonArray.put(json);
				}
				i1 ++;
			}

			json = new JSONObject();

			json.put("searchhistory", jsonArray);

			return json.toString();
		}
		catch(Exception ext)
		{
			Logger.e("putSearchHistory() " + ext.toString());
		}

		return null;
	}
	public static JSONArray putIntegralInfo(HashMap<String, IntegralInfo> hmIntegral, String uid)
	{
		if(!Empty.isEmpty(hmIntegral))
		{
			JSONArray ja = new JSONArray();
			JSONObject json;
			IntegralInfo integral;
			Entry<String, IntegralInfo> entry;
			Iterator<Entry<String, IntegralInfo>> iterator = hmIntegral.entrySet().iterator();
			if(iterator != null)
			{
				while(iterator.hasNext())
				{
					if((entry = iterator.next()) != null)
					{
						if((integral = entry.getValue()) != null)
						{
							json = new JSONObject();
							putString(json, "uuid", integral.sUUid);
							putString(json, "uid", uid);
							putInt(json, "taskTypeId", integral.taskTypeId);
							putInt(json, "emulator", Emulator.isEmulator() ? DR.IMULATOR : DR.REAL_MACHINE);
							ja.put(json);
						}
					}
					
				}
			}
			return ja;
		}
		return null;
	}
	public static JSONArray putIntegralInfo(IntegralInfo integral, String uid)
	{
		if(integral != null)
		{
			JSONArray ja = new JSONArray();
			JSONObject json = new JSONObject();
			putString(json, "uuid", integral.sUUid);
			putString(json, "uid", uid);
			putInt(json, "taskTypeId", integral.taskTypeId);
			putInt(json, "emulator", Emulator.isEmulator() ? DR.IMULATOR : DR.REAL_MACHINE);
			ja.put(json);
			return ja;
		}
		return null;
	}
	public static String putIntegralPayload(JSONArray jar, String sha1) 
	{
		try
		{
			if(jar != null)
			{
				JSONObject jo = new JSONObject();
				putString(jo, "platform", DR.INTEGRAL_PLATFORM);
				putString(jo, "sha1", sha1);
				jo.put("userTaskList", jar);
				return jo.toString();
			}
		} 
		catch (JSONException ext)
		{
			Logger.e("putIntegralPayload() " + ext.toString());
		}
		return null;
	}
	public static IntegralResult getIntegralResult(String inJson) 
	{
		try 
		{
			if(inJson != null)
			{
				IntegralResult intergralResult = new IntegralResult();
				intergralResult.setResultMsg(getResultMsg(inJson));
				JSONArray ja = s2Arr(getString(s2Json(inJson), "uuidList"));
				if(ja != null)
				{
					int i1 = 0;
					String sUUid;
					while(i1 < ja.length())
					{
						if(!Empty.isEmpty((sUUid = ja.getString(i1))))
						{
							intergralResult.addSuccessId(sUUid);
						}
						++ i1;
					}
				}
				return intergralResult;
			}
		} 
		catch (Exception ext)
		{
			Logger.e("getIntegralResult() " + ext.toString());
		}
		return null;
	}
	public static List<SearchHistory> getSearchHistory(final String inJson)
	{
		JSONObject json = s2Json(inJson);
		if(json == null) return null;

		try
		{
			JSONArray jsonArray = s2Arr(getString(json, "searchhistory"));

			if(jsonArray != null)
			{
				List<SearchHistory> list = new ArrayList<>(jsonArray.length());

				JSONObject jsonObject;
				
				String s;

				int i1 = 0;
				while(i1 < jsonArray.length())
				{
					if((jsonObject = jsonArray.getJSONObject(i1)) != null && !Empty.isEmpty(s = getString(jsonObject, "msg")))
					{
						SearchHistory searchHistory = new SearchHistory();
						searchHistory.msg = s;
						searchHistory.time = getLong(jsonObject, "time");
						searchHistory.count = getInt(jsonObject, "count");
						searchHistory.id = getLong(jsonObject, "id");

						list.add(searchHistory);
					}
					i1 ++;
				}

				return !Empty.isEmpty(list) ? list : null;
			}
		}
		catch(Exception ext)
		{
			Logger.e("getSearchHistory() " + ext.toString());
		}

		return null;
	}
	
	private static void putString(final JSONObject json, final String key, final String value)
	{
		try
		{
			if(json == null || Empty.isEmpty(key)) return;

			json.put(key, Empty.isEmpty(value) ? "" : value);
		}
		catch (Exception ext)
		{
			Logger.e("putString() " + ext.toString());
		}
	}
	private static void putInt(final JSONObject json, final String key, final int value)
	{
		try
		{
			if(json == null || Empty.isEmpty(key)) return;

			json.put(key, value);
		}
		catch (Exception ext)
		{
			Logger.e("putInt() " + ext.toString());
		}
	}
	private static void putLong(final JSONObject json, final String key, final long value)
	{
		try
		{
			if(json == null || Empty.isEmpty(key)) return;

			json.put(key, value);
		}
		catch (Exception ext)
		{
			Logger.e("putLong() " + ext.toString());
		}
	}


	public static List<Article> getArticleSearch(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				return getArtList(getString(json, "content"));
			}
		} 
		catch (Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	//TODO 库存查询
	public static StockCoinsResult getStockCoinsResult(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				ResultMsg resultMsg = null;
				if((resultMsg = getResultMsg(getString(json, "msg__"))) != null)
				{
					StockCoinsResult stockCoinsResult = new StockCoinsResult();
					stockCoinsResult.sId = getString(json, "uuid");
					stockCoinsResult.iProductId = getInt(json, "productId");
					stockCoinsResult.sProductName = getString(json, "productName");
					stockCoinsResult.iProductPrice = getInt(json, "productPrice");
					stockCoinsResult.setResultMsgCode(resultMsg);
					return stockCoinsResult;
				}
			}
		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	
	//TODO 订单提交
	public static Order getOrderResult(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				ResultMsg resultMsg = getResultMsg(getString(json, "msg__"));
			
				if(resultMsg != null)
				{
					Order order = new Order();
					order.setResultMsgCode(resultMsg);
					order.sId = getString(json, "uuid");

					JSONObject json1 = s2Json(getString(json, "product"));
					
					if(json1 != null)
					{
						Product product = new Product();
						
						product.sId = getString(json1, "id");
						product.sDetailUrl = getString(json1, "detailUrl");
						product.sSharePicUrl = getString(json1, "sharePicUrl");
						
						order.setProduct(product);
					}
					return order;
				}
			}
		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
	
	//TODO 消息提示
	public static MsgNotification getNotification(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				ResultMsg resultMsg = null;
				if((resultMsg = getResultMsg(getString(json, "msg__"))) != null)
				{
					MsgNotification msgNotification = new MsgNotification();
					msgNotification.setResultMsgCode(resultMsg);
					msgNotification.iTotalCoins = getInt(json, "totalCoins");
					msgNotification.iNewAuthorReplyFlag = getInt(json, "newAuthorReplyFlag");
					msgNotification.iNewPointsFlag = getInt(json, "newPointsFlag");
					msgNotification.sLevelName = getString(json, "levelName");
					msgNotification.sResponseTime = getString(json, "responseTime");
					
					return msgNotification;
				}
			}
		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}

	public static List<Object> getDataObjs(final String inJson)
	{
		try
		{
			JSONObject json = s2Json(inJson);
			if(json != null)
			{
				List<Object> list = new ArrayList<>();
				List<Block> blocksList = getBlocksList(getString(json, "blocks"));
				if(!Empty.isEmpty(blocksList))
				{
					int i1 = 0;
					Block bk = null;
					List<Article> stories = null;
					Article art = null;
					while(i1 < blocksList.size())
					{
						if((bk = blocksList.get(i1)) != null)
						{
							switch (bk.iContentStyle)
							{
							case Block.BLOCK_BANNER_CAROUSEL:
							case Block.BLOCK_BANNER_ROLLNEWS:
							case Block.BLOCK_SCROLL_VIDEO:
								list.add(bk);
								break;
							default:
								if(!Empty.isEmpty(stories = bk.getStories()))
								{
									int i2 = 0;
									while(i2 < stories.size())
									{
										if((art = stories.get(i2)) != null)
										{
											switch (bk.iContentStyle) 
											{
											case Block.BLOCK_NEW_ARTICLE:
												break;
											case Block.BLOCK_NO_IAMGE:
												art.iThumbnailStyle = Article.THUMBNAILSTYPE_NOIMG;
												break;
											case Block.BLOCK_NORMAL:
												art.iThumbnailStyle = Article.THUMBNAILSTYPE_NORMAL;
												break;
											case Block.BLOCK_BIG_IAMGE:
												art.iThumbnailStyle = Article.THUMBNAILSTYPE_BIG;
												break;
											case Block.BLOCK_THREE_IAMGE:
												art.iThumbnailStyle = Article.THUMBNAILSTYPE_THREE;
												break;
											case Block.BLOCK_AD:
												art.iThumbnailStyle = Article.BLOCKTYPE_HARDAD;
												break;
											}
										}
										++ i2;
									}
									list.addAll(stories);
								}
								break;
							}
						}
						++ i1;
					}
				}
				List<Article> storiesList = getArtList(getString(json, "stories"));
				if(!Empty.isEmpty(storiesList))
				{
					list.addAll(storiesList);
				}	
				return list;
			}
		}
		catch(Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}

	public static String putAddressInfo(AddressInfo info)
	{
		if(info != null)
		{
			JSONObject json = new JSONObject();
			putString(json, "name", info.sName);
			putString(json, "address", info.sAddress);
			putString(json, "zipcode", info.sZipCode);
			putString(json, "phonenumber", info.sPhoneNumber);
			return json.toString();
		}
		return null;
	}

	public static AddressInfo getAddressInfo(String inJson)
	{
		JSONObject json = s2Json(inJson);
		if(json != null)
		{
			AddressInfo info = new AddressInfo();
			info.sName = getString(json, "name");
			info.sAddress = getString(json, "address");
			info.sZipCode = getString(json, "zipcode");
			info.sPhoneNumber = getString(json, "phonenumber");
			return info;
		}
		return null;
	}
}
