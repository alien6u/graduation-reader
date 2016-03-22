package com.jayqqaa12.abase.kit.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jayqqaa12.abase.kit.common.TAG;

import android.content.Context;
import android.util.Log;

/**
 * 文件 工具栏
 * 
 */
public class FileKit
{

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The file copy buffer size (10 MB) （原为30MB，为更适合在手机上使用，将其改为10MB，
	 */
	private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 10;

	/**
	 * <p>
	 * 将一个目录下的文件全部拷贝到另一个目录里面，并且保留文件日期。
	 * </p>
	 * <p>
	 * 如果目标目录不存在，则被创建。 如果目标目录已经存在，则将会合并两个文件夹的内容，若有冲突则替换掉目标目录中的文件。
	 * </p>
	 * 
	 * @param srcDir
	 *            源目录，不能为null且必须存在。
	 * @param destDir
	 *            目标目录，不能为null。
	 * @throws NullPointerException
	 *             如果源目录或目标目录为null。
	 * @throws IOException
	 *             如果源目录或目标目录无效。
	 * @throws IOException
	 *             如果拷贝中出现IO错误。
	 */
	public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException
	{
		if (srcDir == null)
		{
			throw new NullPointerException("Source must not be null");
		}
		if (srcDir.exists() && srcDir.isDirectory() == false)
		{
			throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
		}
		if (destDir == null)
		{
			throw new NullPointerException("Destination must not be null");
		}
		if (destDir.exists() && destDir.isDirectory() == false)
		{
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
	}

	/**
	 * <p>
	 * 将目录及其以下子目录拷贝到一个新的位置，并且保留文件日期。
	 * <p>
	 * 如果目标目录不存在，则被创建。 如果目标目录已经存在，则将会合并两个文件夹的内容，若有冲突则替换掉目标目录中的文件。
	 * <p>
	 * 
	 * @param srcDir
	 *            一个存在的源目录，不能为null。
	 * @param destDir
	 *            新的目录，不能为null。
	 * 
	 * @throws NullPointerException
	 *             如果源目录或目标目录为null。
	 * @throws IOException
	 *             如果源目录或目标目录无效。
	 * @throws IOException
	 *             如果拷贝中出现IO错误。
	 */
	public static void copyDirectory(File srcDir, File destDir) throws IOException
	{
		copyDirectory(srcDir, destDir, true);
	}

	/**
	 * 拷贝目录到一个新的位置。
	 * <p>
	 * 该方法将拷贝指定的源目录的所有内容到一个新的目录中。
	 * </p>
	 * <p>
	 * 如果目标目录不存在，则被创建。 如果目标目录已经存在，则将会合并两个文件夹的内容，若有冲突则替换掉目标目录中的文件。
	 * </p>
	 * 
	 * @param srcDir
	 *            一个存在的源目录，不能为null。
	 * @param destDir
	 *            新的目录，不能为null。
	 * 
	 * @throws NullPointerException
	 *             如果源目录或目标目录为null。
	 * @throws IOException
	 *             如果源目录或目标目录无效。
	 * @throws IOException
	 *             如果拷贝中出现IO错误。
	 */
	public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException
	{
		if (srcDir == null)
		{
			throw new NullPointerException("Source must not be null");
		}
		if (srcDir.exists() && srcDir.isDirectory() == false)
		{
			throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
		}
		if (destDir == null)
		{
			throw new NullPointerException("Destination must not be null");
		}
		if (destDir.exists() && destDir.isDirectory() == false)
		{
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath()))
		{
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}

		// 为满足当目标目录在源目录里面的情况。
		List<String> exclusionList = null;
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath()))
		{
			File[] srcFiles = srcDir.listFiles();
			if (srcFiles != null && srcFiles.length > 0)
			{
				exclusionList = new ArrayList<String>(srcFiles.length);
				for (File srcFile : srcFiles)
				{
					File copiedFile = new File(destDir, srcFile.getName());
					exclusionList.add(copiedFile.getCanonicalPath());
				}
			}
		}

		doCopyDirectory(srcDir, destDir, preserveFileDate, exclusionList);
	}

	/**
	 * Internal copy directory method.
	 * 
	 * @param srcDir
	 *            the validated source directory, must not be <code>null</code>
	 * @param destDir
	 *            the validated destination directory, must not be
	 *            <code>null</code>
	 * @param filter
	 *            the filter to apply, null means copy all directories and files
	 * @param preserveFileDate
	 *            whether to preserve the file date
	 * @param exclusionList
	 *            List of files and directories to exclude from the copy, may be
	 *            null
	 * @throws IOException
	 *             if an error occurs
	 * @since Commons IO 1.1
	 */
	private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate, List<String> exclusionList) throws IOException
	{
		// recurse
		File[] srcFiles = srcDir.listFiles();
		if (srcFiles == null)
		{ // null if abstract pathname does not denote a
			// directory, or if an I/O error occurs
			throw new IOException("Failed to list contents of " + srcDir);
		}
		if (destDir.exists())
		{
			if (destDir.isDirectory() == false)
			{
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
			}
		}
		else
		{
			if (!destDir.mkdirs() && !destDir.isDirectory())
			{
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
		}
		if (destDir.canWrite() == false)
		{
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}
		for (File srcFile : srcFiles)
		{
			File dstFile = new File(destDir, srcFile.getName());
			if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath()))
			{
				if (srcFile.isDirectory())
				{
					doCopyDirectory(srcFile, dstFile, preserveFileDate, exclusionList);
				}
				else
				{
					doCopyFile(srcFile, dstFile, preserveFileDate);
				}
			}
		}

		// Do this last, as the above has probably affected directory metadata
		if (preserveFileDate)
		{
			destDir.setLastModified(srcDir.lastModified());
		}
	}

	/**
	 * Internal copy file method.
	 * 
	 * @param srcFile
	 *            the validated source file, must not be <code>null</code>
	 * @param destFile
	 *            the validated destination file, must not be <code>null</code>
	 * @param preserveFileDate
	 *            whether to preserve the file date
	 * @throws IOException
	 *             if an error occurs
	 */
	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException
	{
		if (destFile.exists() && destFile.isDirectory())
		{
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel input = null;
		FileChannel output = null;
		try
		{
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			input = fis.getChannel();
			output = fos.getChannel();
			long size = input.size();
			long pos = 0;
			long count = 0;
			while (pos < size)
			{
				count = (size - pos) > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : (size - pos);
				pos += output.transferFrom(input, pos, count);
			}
		}
		finally
		{
			IOKit.closeQuietly(output);
			IOKit.closeQuietly(fos);
			IOKit.closeQuietly(input);
			IOKit.closeQuietly(fis);
		}

		if (srcFile.length() != destFile.length())
		{
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
		}
		if (preserveFileDate)
		{
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	/**
	 * Opens a {@link FileInputStream} for the specified file, providing better
	 * error messages than simply calling <code>new FileInputStream(file)</code>
	 * .
	 * <p>
	 * At the end of the method either the stream will be successfully opened,
	 * or an exception will have been thrown.
	 * <p>
	 * An exception is thrown if the file does not exist. An exception is thrown
	 * if the file object exists but is a directory. An exception is thrown if
	 * the file exists but cannot be read.
	 * 
	 * @param file
	 *            the file to open for input, must not be <code>null</code>
	 * @return a new {@link FileInputStream} for the specified file
	 * @throws FileNotFoundException
	 *             if the file does not exist
	 * @throws IOException
	 *             if the file object is a directory
	 * @throws IOException
	 *             if the file cannot be read
	 * @since Commons IO 1.3
	 */
	public static FileInputStream openInputStream(File file) throws IOException
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false)
			{
				throw new IOException("File '" + file + "' cannot be read");
			}
		}
		else
		{
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	/**
	 * Reads the contents of a file line by line to a List of Strings. The file
	 * is always closed.
	 * 
	 * @param file
	 *            the file to read, must not be <code>null</code>
	 * @param encoding
	 *            the encoding to use, <code>null</code> means platform default
	 * @return the list of Strings representing each line in the file, never
	 *         <code>null</code>
	 * @throws IOException
	 *             in case of an I/O error
	 * @throws java.io.UnsupportedEncodingException
	 *             if the encoding is not supported by the VM
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(File file, String encoding) throws IOException
	{
		InputStream in = null;
		try
		{
			in = openInputStream(file);
			return readLines(in, encoding);
		}
		finally
		{
			IOKit.closeQuietly(in);
		}
	}

	/**
	 * Reads the contents of a file line by line to a List of Strings using the
	 * default encoding for the VM. The file is always closed.
	 * 
	 * @param file
	 *            the file to read, must not be <code>null</code>
	 * @return the list of Strings representing each line in the file, never
	 *         <code>null</code>
	 * @throws IOException
	 *             in case of an I/O error
	 * @since Commons IO 1.3
	 */
	public static List<String> readLines(File file) throws IOException
	{
		return readLines(file, null);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the default character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(InputStream input) throws IOException
	{
		InputStreamReader reader = new InputStreamReader(input);
		return readLines(reader);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at <a
	 * href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @param encoding
	 *            the encoding to use, null means platform default
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(InputStream input, String encoding) throws IOException
	{
		if (encoding == null)
		{
			return readLines(input);
		}
		else
		{
			InputStreamReader reader = new InputStreamReader(input, encoding);
			return readLines(reader);
		}
	}

	/**
	 * Get the contents of a <code>Reader</code> as a list of Strings, one entry
	 * per line.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * 
	 * @param input
	 *            the <code>Reader</code> to read from, not null
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(Reader input) throws IOException
	{
		BufferedReader reader = new BufferedReader(input);
		List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null)
		{
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	public static String[] getAllAssertFileName(Context context)
	{
		String[] files = null;
		try
		{
			files = context.getAssets().list("");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return files;
	}

	/**
	 * 返回 后缀名 为 。rule 的文件 名
	 * 
	 * @param files
	 *            要匹配的文件
	 * @param rule
	 *            匹配规则
	 * @return 返回 后缀名 为 。rule 的文件 名
	 */
	public static List<String> getFiles(String[] files, String rule)
	{

		List<String> ruleFiles = new ArrayList<String>();

		for (String file : files)
		{
			if (file.endsWith(rule))
			{
				ruleFiles.add(file);
			}
		}
		return ruleFiles;
	}

	/**
	 * 
	 * 输入 要检查 的文件名列表 检查 zip 文件 是否解压到 目标目录
	 * 
	 * @param files
	 *            检查的zip 文件
	 * @param toPath
	 *            解压的目录路径
	 * @return 是否解压到 目标文件
	 * @throws FileNotFoundException
	 */

	public static boolean isFileDecompression(String[] files, String toPath) throws FileNotFoundException
	{
		return isFileDecompression(files, toPath, ".zip");
	}

	/**
	 * 
	 * 输入 要检查 的文件名列表 检查 zip 文件 是否解压到 目标目录
	 * 
	 * @param files
	 *            检查的zip 文件
	 * @param toPath
	 *            解压的目录路径
	 * @param rule
	 *            要检测文件的后缀名 可以自定义 不一定是 。zip
	 * @return 是否解压到 目标文件
	 * @throws FileNotFoundException
	 */
	public static boolean isFileDecompression(String[] files, String toPath, String rule) throws FileNotFoundException
	{
		boolean result = true;

		if (files.length == 0)
			throw new FileNotFoundException();

		List<String> zipFiles = getFiles(files, rule);

		for (String zipFile : zipFiles)
		{
			String dbFile = zipFile.replace(rule, ".db");
			File file = new File(toPath + File.separator + dbFile);

			if (!file.exists())
			{
				Log.i(TAG.UTIL,dbFile+" 没有找到");
				result = false;
			}

		}

		return result;

	}

	/**
	 *  拷贝 指定 的 单个asset 文件到指定目录
	 * @param context
	 * @param assetsFileNmae
	 * @param outDirName
	 * @throws IOException
	 */
	public static void copyAssetsFile(Context context, String assetsFileNmae, String outDirName) throws IOException
	{
		// 判断目录是否存在。如不存在则创建一个目录
		File outDir = new File(outDirName);
		if (!outDir.exists() || !outDir.isDirectory())
		{
			outDir.mkdir();
		}
		// 建立输出 文件
		File outFile = new File(outDirName + File.separator + assetsFileNmae);
		if (!outFile.exists())
		{
			outFile.createNewFile();
		}
		InputStream is = context.getAssets().open(assetsFileNmae);
		OutputStream os = new FileOutputStream(outFile);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0)
		{
			os.write(buffer, 0, length);
		}
		
		os.flush();
		os.close();
		is.close();
	}

	/**
	 * 查到 文件夹 下面 所以 文件 非递归方式
	 * 
	 */
	public static LinkedList<File> listLinkedFiles(String strPath)
	{
		LinkedList<File> list = new LinkedList<File>();
		File dir = new File(strPath);
		File file[] = dir.listFiles();
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].isDirectory())
				list.add(file[i]);
			else
				System.out.println(file[i].getAbsolutePath());
		}
		File tmp;
		while (!list.isEmpty())
		{
			tmp = (File) list.removeFirst();
			if (tmp.isDirectory())
			{
				file = tmp.listFiles();
				if (file == null)
					continue;
				for (int i = 0; i < file.length; i++)
				{
					if (file[i].isDirectory())
						list.add(file[i]);
					else
						System.out.println(file[i].getAbsolutePath());
				}
			}
			else
			{
				System.out.println(tmp.getAbsolutePath());
			}
		}
		return list;
	}

	/**
	 * 查到 文件夹 下面 所以 文件 递归方式
	 * 
	 */

	public static ArrayList<File> listFiles(String strPath)
	{
		return refreshFileList(strPath);
	}

	private static ArrayList<File> refreshFileList(String strPath)
	{
		ArrayList<File> filelist = new ArrayList<File>();
		File dir = new File(strPath);
		File[] files = dir.listFiles();

		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isDirectory())
			{
				refreshFileList(files[i].getAbsolutePath());
			}
			else
			{
				if (files[i].getName().toLowerCase().endsWith("zip"))
					filelist.add(files[i]);
			}
		}
		return filelist;
	}

	public static boolean deleteFile(String fileName)
	{
		File file = new File(fileName);
		if (file.exists()) return file.delete();
		else return false;
	}
	
	 /**  
     * 删除目录（文件夹）以及目录下的文件  
     * @param   dir 被删除目录的文件路径  
     * @return  目录删除成功返回true,否则返回false  
     */  
    public static boolean deleteDirectory(String dir){   
        //如果dir不以文件分隔符结尾，自动添加文件分隔符   
        if(!dir.endsWith(File.separator)){   
            dir = dir+File.separator;   
        }   
        File dirFile = new File(dir);   
        //如果dir对应的文件不存在，或者不是一个目录，则退出   
        if(!dirFile.exists() || !dirFile.isDirectory()){   
            return false;   
        }   
        boolean flag = true;   
        //删除文件夹下的所有文件(包括子目录)   
        File[] files = dirFile.listFiles();   
        for(int i=0;i<files.length;i++){   
            //删除子文件   
            if(files[i].isFile()){   
                flag = deleteFile(files[i].getAbsolutePath());   
                if(!flag){   
                    break;   
                }   
            }   
            //删除子目录   
            else{   
                flag = deleteDirectory(files[i].getAbsolutePath());   
                if(!flag){   
                    break;   
                }   
            }   
        }   

        if(!flag){   
            return false;   
        }   
        //删除当前目录   
        if(dirFile.delete()){   
            return true;   
        }else{   
            return false;   
        }   
    }   
}