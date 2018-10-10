package cd.s.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hf.http.util.Empty;

public class Translate extends DataBase
{
	private final String URL_PRONUNCIATION = "http://res.iciba.com/resource/amp3";
	
	public static final String PREFIX_PH = "ph_id";
	public static final String PREFIX_SYMBOL = "symbol_id";
	
	public static final int LAUGUAGE_EN = 1;
	public static final int LAUGUAGE_ZH = 2;
	
	private
	List<TranslateExampleItem>
			translateExampleItems;
	
	private
	List<TranslatePhraseItem>
			ltPhrases,
			ltIdioms;
	
	private
	List<TranslateSymbolsItem>
			translateSymbolsItems;
	
	public
	String
			sWord;
	
	public
	int
		iLauguage;
	
	public class TranslateSymbolsItem
	{
		public
		String
				sPronunciationEn,
				sPronunciationAm,
				sPhoneticEn,
				sPhoneticAm,
				sPinYin;
		
		private
		List<TranslateItem>
				translateItems;
		
		public void addTranslateItem(TranslateItem item)
		{
			if(item != null)
			{
				if(translateItems == null)
				{
					translateItems = new ArrayList<>();
				}
				
				translateItems.add(item);
			}
		}
		
		public List<TranslateItem> geTranslateItems()
		{
			return translateItems;
		}
		
		public void setPronunciationEn(String sPronunciationEn)
		{
			if(!Empty.isEmpty(sPronunciationEn))
			{
				if(!sPronunciationEn.startsWith(File.separator))
				{
					sPronunciationEn = File.separator + sPronunciationEn;
				}
				this.sPronunciationEn = URL_PRONUNCIATION + sPronunciationEn;
			}
		}
		
		public void setPronunciationAm(String sPronunciationAm)
		{
			if(!Empty.isEmpty(sPronunciationAm))
			{
				if(!sPronunciationAm.startsWith(File.separator))
				{
					sPronunciationAm = File.separator + sPronunciationAm;
				}
				this.sPronunciationAm = URL_PRONUNCIATION + sPronunciationAm;
			}
		}
		
		public String getPronunciationEn()
		{
			return sPronunciationEn;
		}
		
		public String getPronunciationAm()
		{
			return sPronunciationAm;
		}
	}
	
	public class TranslatePhraseItem
	{
		public
		String
				sName;
		
		private
		List<TranslateMeanItem>
				ltMeanItems;
		
		public void addTranslateMeanItem(TranslateMeanItem item)
		{
			if(item != null)
			{
				if(ltMeanItems == null)
				{
					ltMeanItems = new ArrayList<>();
				}
				
				ltMeanItems.add(item);
			}
		}
		
		public List<TranslateMeanItem> getTranslateMeanItems()
		{
			return ltMeanItems;
		}
	}
	
	public class TranslateMeanItem
	{
		public
		String
				sMeanEn,
				sMeanCn;
		
		private
		List<TranslateExampleItem>
				translateExampleItems;
		
		public void addTranslateExampleItem(TranslateExampleItem item)
		{
			if(item != null)
			{
				if(translateExampleItems == null)
				{
					translateExampleItems = new ArrayList<>();
				}
				
				translateExampleItems.add(item);
			}
		}
		
		public List<TranslateExampleItem> getTranslateExampleItems()
		{
			return translateExampleItems;
		}
	}
	
	public class TranslateExampleItem
	{
		public
		String
				sExampleEn,
				sExampleCn;
	}
	
	public void addTranslatePhrase(TranslatePhraseItem item)
	{
		if(item != null)
		{
			if(ltPhrases == null)
			{
				ltPhrases = new ArrayList<>();
			}
			
			ltPhrases.add(item);
		}
	}
	
	public List<TranslatePhraseItem> geTranslatePhrases()
	{
		return ltPhrases;
	}
	
	public void addTranslateIdiom(TranslatePhraseItem item)
	{
		if(item != null)
		{
			if(ltIdioms == null)
			{
				ltIdioms = new ArrayList<>();
			}
			
			ltIdioms.add(item);
		}
	}
	
	public List<TranslatePhraseItem> getTranslateIdioms()
	{
		return ltIdioms;
	}
	
	public void addTranslateSymbolsItem(TranslateSymbolsItem item)
	{
		if(item != null)
		{
			if(translateSymbolsItems == null)
			{
				translateSymbolsItems = new ArrayList<>();
			}
			
			translateSymbolsItems.add(item);
		}
	}
	
	public List<TranslateSymbolsItem> getTranslateSymbolsItems()
	{
		return translateSymbolsItems;
	}
	
	public void addTranslateExampleItem(TranslateExampleItem item)
	{
		if(item != null)
		{
			if(translateExampleItems == null)
			{
				translateExampleItems = new ArrayList<>();
			}
			
			translateExampleItems.add(item);
		}
	}
	
	public List<TranslateExampleItem> getTranslateExampleItems()
	{
		return translateExampleItems;
	}
}
