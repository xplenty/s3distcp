/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Map;
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.collect.ImmutableMap;
/*    */ 
/*    */ public class Utils
/*    */ {
/*    */   public static String randomString(long value)
/*    */   {
/* 10 */     StringBuffer result = new StringBuffer();
/*    */ 
/* 13 */     if (value < 0L)
/* 14 */       value = -value;
/*    */     do
/*    */     {
/* 17 */       long remainder = value % 58L;
/*    */       int c;
/* 18 */       if (remainder < 24L) {
/* 19 */         c = 'a' + (char)(int)remainder;
/*    */       }
/*    */       else
/*    */       {
/* 21 */         if (remainder < 48L) {
/* 22 */           c = 'A' + (char)(int)(remainder - 24L);
/*    */         }
/*    */         else
/* 25 */           c = '0' + (char)(int)(remainder - 48L);
/*    */       }
/* 27 */       result.appendCodePoint(c);
/* 28 */       value /= 58L;
/*    */     }
/* 30 */     while (value > 0L);
/* 31 */     return result.reverse().toString();
/*    */   }
/*    */ 
/*    */   public static String randomString() {
/* 35 */     return randomString(new SecureRandom().nextLong());
/*    */   }
/*    */ 
/*    */   public static String getSuffix(String name) {
/* 39 */     if (name != null) {
/* 40 */       String[] parts = name.split("\\.");
/* 41 */       if (parts.length > 1) {
/* 42 */         return parts[(parts.length - 1)];
/*    */       }
/*    */     }
/* 45 */     return "";
/*    */   }
/*    */ 
/*    */   public static String replaceSuffix(String name, String suffix) {
/* 49 */     if (getSuffix(name).equals("")) {
/* 50 */       return name + suffix;
/*    */     }
/* 52 */     int index = name.lastIndexOf('.');
/* 53 */     return name.substring(0, index) + suffix;
/*    */   }
/*    */ 
/*    */   public static boolean isS3Scheme(String scheme)
/*    */   {
/* 58 */     return (scheme.equals("s3")) || (scheme.equals("s3n")) || (scheme.equals("s3a"));
/*    */   }
/*    */ 
/*    */   public static ThreadPoolExecutor createDefaultExecutorService() {
/* 62 */     ThreadFactory threadFactory = new ThreadFactory() {
/* 63 */       private int threadCount = 1;
/*    */ 
/*    */       public Thread newThread(Runnable r) {
/* 66 */         Thread thread = new Thread(r);
/* 67 */         thread.setName("s3-transfer-manager-worker-" + this.threadCount++);
/* 68 */         return thread;
/*    */       }
/*    */     };
/* 71 */     return (ThreadPoolExecutor)Executors.newFixedThreadPool(10, threadFactory);
/*    */   }

    public static String escapePath(String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith("s3://") || path.startsWith("s3n://") || path.startsWith("s3a://") ) {
            return path;
        }
        StringBuilder result = new StringBuilder();
        String[] components = path.split("/");
        for (String component : components) {
        	result.append(customUrlEncode(component)).append("/");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }
    
    static final Map<String, String> CHAR_MAPPINGS = ImmutableMap.of(
    	    " ", "%20",
    	    "[", "%5B",
    	    "]", "%5D",
    	    ":", "%3A"
    	);
    
    private static String customUrlEncode(String part) {
    	for (Map.Entry<String, String> e : CHAR_MAPPINGS.entrySet()){
    		part = part.replace(e.getKey(), e.getValue());    		
    	}
    	return part;
    }
    
    private static String customUrlDecode(String part) {
    	for (Map.Entry<String, String> e : CHAR_MAPPINGS.entrySet()){
    		part = part.replace(e.getValue(), e.getKey());    		
    	}
    	return part;
    }

    public static String unescapePath(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("s3://") && !path.startsWith("s3n://") && !path.startsWith("s3a://")) {
            return path;
        }
        StringBuilder result = new StringBuilder();
        String[] components = path.split("/");
        for (String component : components) {
            result.append(customUrlDecode(component)).append("/");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.Utils
 * JD-Core Version:    0.6.2
 */