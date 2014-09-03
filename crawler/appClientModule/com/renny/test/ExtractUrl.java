package com.renny.test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractUrl {
	public static Pattern pattern1 =Pattern.compile("[^\\s]*((<\\s*[aA]\\s+(href\\s*=[^>]+\\s*)>)(.*)</[aA]>).*", Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
	public static Pattern pattern2 =Pattern.compile(".*(<\\s*[aA]\\s+(href\\s*=[^>]+\\s*)>(.*)</[aA]>).*", Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
	public static Pattern pattern3 =Pattern.compile(".*href\\s*=\\s*(\"|'|)http://.*", Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
	
	public static void parseUrl(Set<String> set, String var) {
		Matcher matcher =null;
		if (var !=null && var.length() >28) {
			matcher =pattern3.matcher(var);
			if (matcher !=null && matcher.matches()) {
				matcher =pattern1.matcher(var);
				String aString =null, bString =null, url =null;
				while (matcher !=null && matcher.find()) {
					if (matcher.groupCount() >3) {
						bString =matcher.group(matcher.groupCount() -3);
						aString =matcher.group(matcher.groupCount() -2);
						url =matcher.group(matcher.groupCount() -1);
						if (url !=null)set.add(url);
						bString =bString.replaceAll(aString, "");
					}
				}
				if (bString !=null) parseUrl(set, bString);
			}
		}
	}
	
	public static String addProperty(Set<String> set, String var) {
		String result =var;
		for (String url : set) {
			result =result.replaceAll(url, url +" target=\"_blank\"");
		}
		return result;
	}
	
	public static void main(String[] args) {
		String str ="this is test<a Href=\"http://www.google.com\">Google</a>this is really test\n";
		str +="this is test<a Href=\"http://www.so.com\">360搜索+</a>this is really test";
		Set<String> set =new HashSet<String>();
		parseUrl(set, str);
		System.out.println(addProperty(set, str));
	}
}
