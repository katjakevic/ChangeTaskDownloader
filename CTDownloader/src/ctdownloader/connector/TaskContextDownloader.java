package ctdownloader.connector;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class TaskContextDownloader {
	
	/**
	 * Downloads the task context of a the specified TaskAttribute and stores it locally. The
	 * task context is extracted from a zip file.
	 * 
	 * @see TaskContextModel
	 * @param attribute
	 * @return a list of the task contexts of a bugzilla bug report
	 */
	public String downloadTaskContext(TaskAttribute attribute) {

		String fileName = "MylynContexts/"+attribute.getTaskData().getTaskId() +".zip";
		Map<String, TaskAttribute> attrs = attribute.getAttributes();
		for (Map.Entry<String, TaskAttribute> entry : attrs.entrySet()) {
			if (entry.getKey().contains("task.common.attachment")) {
				TaskAttribute attachmentAttr = entry.getValue();

				TaskAttribute descAttr = attachmentAttr.getAttribute("desc");
				String attchFileName = descAttr.getValue();
				if (attchFileName.equals("mylyn/context/zip")) {

					TaskAttribute urlAttr = attachmentAttr
							.getAttribute("task.common.attachment.url");
					try {

						String urlString = urlAttr.getValue();
						URL url = new URL(urlString);

						URLConnection conn = url.openConnection();
						InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(fileName);
				        byte[] b = new byte[1024];
				        int count;
				        while ((count = in.read(b)) >= 0) {
				            out.write(b, 0, count);
				        }
				        out.flush(); out.close(); in.close(); 
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return fileName;
	}

}
