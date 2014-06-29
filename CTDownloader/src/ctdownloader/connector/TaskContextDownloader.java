package ctdownloader.connector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

import ctdownloader.exceptions.TaskContextDownloadException;

public class TaskContextDownloader {

	/**
	 * Downloads the task context of a the specified TaskAttribute and stores it
	 * locally. The task context is extracted from a zip file.
	 * 
	 * @see TaskContextModel
	 * @param attribute
	 * @return a map containing the path to the task context belonging to the
	 *         task together with the submitter
	 * @throws TaskContextDownloadException
	 */
	public Map<String, String> downloadTaskContext(TaskAttribute attribute)
			throws TaskContextDownloadException {

		// filepath, submitter
		Map<String, String> taskContextMap = new HashMap<>();

		// check if the directory to download all task contexts is existing
		File f = new File("MylynContexts");
		if (!f.exists()) {
			System.out.println(f.getAbsolutePath());
			boolean isCreated = f.mkdir();
			if (isCreated == false) {
				throw new TaskContextDownloadException();
			}
		}

		Map<String, TaskAttribute> attrs = attribute.getAttributes();

		int counter = 0;
		for (Map.Entry<String, TaskAttribute> entry : attrs.entrySet()) {
			if (entry.getKey().contains("task.common.attachment")) {
				TaskAttribute attachmentAttr = entry.getValue();

				TaskAttribute descAttr = attachmentAttr.getAttribute("desc");
				String attchFileName = descAttr.getValue();
				if (attchFileName.equals("mylyn/context/zip")) {

					// check if the directory to download these task contexts is
					// existing
					File tcDir = new File("MylynContexts/"
							+ attribute.getTaskData().getTaskId());
					if (!tcDir.exists()) {
						System.out.println(tcDir.getAbsolutePath());
						boolean isCreated = tcDir.mkdir();
						if (isCreated == false) {
							throw new TaskContextDownloadException();
						}
					}

					String tcName = tcDir.getPath() + "\\"
							+ attribute.getTaskData().getTaskId() + "_"
							+ counter + ".zip";

					TaskAttribute urlAttr = attachmentAttr
							.getAttribute("task.common.attachment.url");

					String submitter = attachmentAttr.getAttribute(
							TaskAttribute.ATTACHMENT_AUTHOR).getValue();

					try {

						String urlString = urlAttr.getValue();
						URL url = new URL(urlString);

						URLConnection conn = url.openConnection();
						InputStream in = conn.getInputStream();

						FileOutputStream out = new FileOutputStream(tcName);
						byte[] b = new byte[1024];
						int count;
						while ((count = in.read(b)) >= 0) {
							out.write(b, 0, count);
						}
						out.flush();
						out.close();
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					taskContextMap.put(tcName, submitter);
					counter++;
				}
			}
		}
		return taskContextMap;
	}

}
