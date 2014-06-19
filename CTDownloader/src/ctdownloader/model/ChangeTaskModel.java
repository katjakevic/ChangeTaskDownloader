package ctdownloader.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.core.LocalContextStore;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;



@SuppressWarnings("restriction")
public class ChangeTaskModel {
	
	private String summary;
	private String description;
	private ArrayList<CommentModel> comments = new ArrayList<CommentModel>();
	private String contextFileName;
	private int id;

	private String product;
	private String severity;
	private String priority;

	private String changed;

	public ChangeTaskModel(String taskSummary, String taskDesc,
			ArrayList<CommentModel> taskComments, int taskId,
			String contextFileName, String product, String severity,
			String priority) {
		this.summary = taskSummary;
		this.description = taskDesc;
		this.comments.addAll(taskComments);
		this.id = taskId;
		this.contextFileName = contextFileName;
		this.priority = priority;
		this.product = product;
		this.severity = severity;

	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getChanged() {
		return changed;
	}

	public void setChanged(String changed) {
		this.changed = changed;
	}


	public String getContextFileName() {
		return contextFileName;
	}

	public void setContextFileName(String contextFileName) {
		this.contextFileName = contextFileName;
	}

	public boolean hasTaskContext() {
		File file = new File(contextFileName);
		return file.exists();
	}

	@SuppressWarnings("restriction")
	public IInteractionContext getTaskContext() {
		IInteractionContext interactionContext = null;
		File file = new File("MylynContexts/" + id + ".zip");
		LocalContextStore store = new LocalContextStore(
				new InteractionContextScaling());
		interactionContext = store.loadContext(String.valueOf(id), file,
				new InteractionContextScaling());
		return interactionContext;
	}

	public ArrayList<String> getMethodsFromContext() {
		ArrayList<String> methods = new ArrayList<>();
		if (hasTaskContext()) {
			IInteractionContext context = getTaskContext();
			List<IInteractionElement> elements = context.getInteresting();

			for (IInteractionElement element : elements) {
				if (element.getHandleIdentifier().contains("~")) {

					String className = getClassNameInStructureHandle(element
							.getHandleIdentifier());
					String methodName = getMethodNameInStrucutreHandle(element
							.getHandleIdentifier());
					if (!className.isEmpty() && !methodName.isEmpty()) {
						methods.add(className + "." + methodName);
					}
				}
			}
		}
		return methods;
	}

	public ArrayList<String> getFilesFromContext() {
		ArrayList<String> files = new ArrayList<>();
		if (hasTaskContext()) {
			IInteractionContext context = getTaskContext();
			List<IInteractionElement> elements = context.getInteresting();

			for (IInteractionElement element : elements) {

				if (element.getContentType().equals("java")) {
					String handle = element.getHandleIdentifier();
					String className = getClassNameInStructureHandle(handle);
					System.out.println(className);
					if (!className.isEmpty()) {
						files.add(className);
					}

				}
			}
		}

		return files;
	}

	public Set<Element> getAllSelectionManipulationElements() {
		Set<Element> elements = new HashSet<>();

		if (hasTaskContext()) {
			IInteractionContext context = getTaskContext();
			List<InteractionEvent> history = context.getInteractionHistory();

			ArrayList<InteractionEvent> edits = new ArrayList<>();
			for (InteractionEvent event : history) {
				if (event.getKind() == Kind.EDIT) {
					edits.add(event);
				}
			}

			for (InteractionEvent event : history) {
				if (event.getKind() == Kind.MANIPULATION
						|| event.getKind() == Kind.SELECTION) {
					if (event.getStructureKind().equals("java")) {
						if (!inEditList(edits, event)) {

							String handle = event.getStructureHandle();
							// method element
							if (handle.contains("~")) {
								String className = getClassNameInStructureHandle(handle);
								String methodName = getMethodNameInStrucutreHandle(handle);
								if (!className.isEmpty()
										&& !methodName.isEmpty()) {
									// elements.add(className + "." +
									// methodName);
									MethodElement element = new MethodElement(
											className, methodName);
									elements.add(element);

									if (!isClassInEditList(edits, className)) {
										ClassElement cElement = new ClassElement(
												className);
										elements.add(cElement);
									}

								}
							} else {// class element
								String className = getClassNameInStructureHandle(handle);
								if (!className.isEmpty()) {
									ClassElement element = new ClassElement(
											className);
									elements.add(element);
								}
							}
						}
					}

				}
			}

		}

		return elements;
	}

	private boolean isClassInEditList(ArrayList<InteractionEvent> edits,
			String className) {
		for (InteractionEvent ev : edits) {
			String handle1 = ev.getStructureHandle();
			String className1 = getClassNameInStructureHandle(handle1);
			if (className1.equals(className)) {
				return true;
			}
		}
		return false;
	}

	private boolean inEditList(ArrayList<InteractionEvent> edits,
			InteractionEvent event) {
		String handle = event.getStructureHandle();
		if (handle.contains("~")) {
			String className = getClassNameInStructureHandle(handle);
			String methodName = getMethodNameInStrucutreHandle(handle);
			for (InteractionEvent ev : edits) {
				String handle1 = ev.getStructureHandle();
				String className1 = getClassNameInStructureHandle(handle1);
				String methodName1 = getMethodNameInStrucutreHandle(handle1);
				if (className1.equals(className)
						&& methodName1.equals(methodName)) {
					return true;
				}
			}
		} else {
			String className = getClassNameInStructureHandle(handle);
			for (InteractionEvent ev : edits) {
				String handle1 = ev.getStructureHandle();
				String className1 = getClassNameInStructureHandle(handle1);
				if (className1.equals(className)) {
					return true;
				}
			}

		}

		return false;
	}

	public ArrayList<InteractionEvent> getSelectionEditManipulationSequence() {
		ArrayList<InteractionEvent> sequence = new ArrayList<>();
		IInteractionContext context = getTaskContext();
		List<InteractionEvent> history = context.getInteractionHistory();

		Collections.sort(history, new CustomComparator());

		for (InteractionEvent event : history) {

			if (event.getKind() == Kind.EDIT
					|| event.getKind() == Kind.MANIPULATION
					|| event.getKind() == Kind.SELECTION) {
				if (event.getStructureKind().equals("java")) {
					sequence.add(event);
				}
			}
		}
		return sequence;
	}

	public long getProcessTime() {
		ArrayList<InteractionEvent> sequence = getSelectionEditManipulationSequence();

		if (!sequence.isEmpty()) {

			InteractionEvent first = sequence.get(0);
			InteractionEvent last = sequence.get(sequence.size() - 1);

			long diff = last.getDate().getTime() - first.getDate().getTime();
			long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
			return seconds;
		}
		return 0;
	}

	public ArrayList<Element> getInteractionSequences() {

		ArrayList<Element> sequence = new ArrayList<>();

		IInteractionContext context = getTaskContext();
		List<InteractionEvent> history = context.getInteractionHistory();

		Collections.sort(history, new CustomComparator());

		for (InteractionEvent event : history) {

			if (event.getKind() == Kind.EDIT
					|| event.getKind() == Kind.MANIPULATION
					|| event.getKind() == Kind.SELECTION) {
				if (event.getStructureKind().equals("java")) {
					String handle = event.getStructureHandle();
					System.out.println(event.getDate().toString());
					// method element
					if (handle.contains("~")) {
						String className = getClassNameInStructureHandle(handle);
						String methodName = getMethodNameInStrucutreHandle(handle);
						if (!className.isEmpty() && !methodName.isEmpty()) {
							// elements.add(className + "." + methodName);
							MethodElement element = new MethodElement(
									className, methodName);
							sequence.add(element);

						}
					} else {// class element
						String className = getClassNameInStructureHandle(handle);
						if (!className.isEmpty()) {
							ClassElement element = new ClassElement(className);
							sequence.add(element);
						}
					}
				}

			}

		}

		return sequence;
	}

	public class CustomComparator implements Comparator<InteractionEvent> {
		@Override
		public int compare(InteractionEvent o1, InteractionEvent o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	}

	public Set<Element> getAllSelectionEditManipulationElements() {
		Set<Element> elements = new HashSet<>();

		if (hasTaskContext()) {
			IInteractionContext context = getTaskContext();
			List<InteractionEvent> history = context.getInteractionHistory();
			for (InteractionEvent event : history) {
				if (event.getKind() == Kind.EDIT
						|| event.getKind() == Kind.MANIPULATION
						|| event.getKind() == Kind.SELECTION) {
					if (event.getStructureKind().equals("java")) {
						String handle = event.getStructureHandle();
						// method element
						if (handle.contains("~")) {
							String className = getClassNameInStructureHandle(handle);
							String methodName = getMethodNameInStrucutreHandle(handle);
							if (!className.isEmpty() && !methodName.isEmpty()) {
								// elements.add(className + "." + methodName);
								MethodElement element = new MethodElement(
										className, methodName);
								elements.add(element);
								ClassElement cElement = new ClassElement(
										className);
								elements.add(cElement);
							}
						} else {// class element
							String className = getClassNameInStructureHandle(handle);
							if (!className.isEmpty()) {
								ClassElement element = new ClassElement(
										className);
								elements.add(element);
							}
						}
					}

				}
			}

		}

		return elements;
	}

	public Set<Element> getAllEditElements() {
		Set<Element> elements = new HashSet<>();

		if (hasTaskContext()) {
			IInteractionContext context = getTaskContext();
			List<InteractionEvent> history = context.getInteractionHistory();
			for (InteractionEvent event : history) {
				if (event.getKind() == Kind.EDIT) {
					if (event.getStructureKind().equals("java")) {
						String handle = event.getStructureHandle();
						// method element
						if (handle.contains("~")) {
							String className = getClassNameInStructureHandle(handle);
							String methodName = getMethodNameInStrucutreHandle(handle);
							if (!className.isEmpty() && !methodName.isEmpty()) {
								// elements.add(className + "." + methodName);
								MethodElement element = new MethodElement(
										className, methodName);
								elements.add(element);
								ClassElement cElement = new ClassElement(
										className);
								elements.add(cElement);
							}
						} else {// class element
							String className = getClassNameInStructureHandle(handle);
							if (!className.isEmpty()) {
								ClassElement element = new ClassElement(
										className);
								elements.add(element);
							}
						}
					}

				}
			}

		}

		return elements;
	}

	public ArrayList<String> splitCode(String code) {
		ArrayList<String> splitted = new ArrayList<>();

		String[] splits = code.split("(?=[A-Z][a-z])");

		for (String s : splits) {
			String[] splits2 = s.split("(?=\\()");
			for (String s2 : splits2) {
				if (!s2.isEmpty()) {
					splitted.add(s2);
				}
			}
		}

		return splitted;
	}

	public boolean containsCodeSnippet(String text) {
		boolean containsCamelCase = false;

		String regex1 = "[a-z]+[.|0-9|a-z]*[A-Z]+[a-zA-Z | .|0-9| (| ) |, | _ | < | > |:|$]*";
		String regex2 = "[A-Z]+[A-Za-z|.| #]*[a-z]+[A-Z]+[A-Za-z|.|: | (]*";
		String regex3 = "[A-Z]+[A-Z]+[a-z]+[A-Za-z|.]*";

		String regex4 = "[!]{0,1}[(]{0,1}[@]{0,1}" + regex2
				+ "[)]{0,1}[@]{0,1}[,]{0,1}";
		String regex9 = "[!]{0,1}[(]{0,1}[@]{0,1}[\"]{0,1}" + regex1
				+ "[)]{0,1}[@]{0,1}[\"]{0,1}";
		String regex5 = "[\"]" + regex1 + "[\"] ";
		String regex6 = "[\"]" + regex1 + "[\"][.]";

		String regexInsideMethodBraces = "[(][A-Za-z| .|:|0-9 | , | ( | ) | \" ]*[)]";
		String regex7 = "[a-zA-Z| .|( |)]*[a-z]+[)]{0,1}[.][a-z]+[a-zA-Z| 0-9| . | \\$ | ( | )]*"
				+ regexInsideMethodBraces + "[;]{0,1}";
		String regex11 = regex2 + regexInsideMethodBraces + "[;]{0,1}";
		String regex8 = regex1 + "[:]";
		String regex10 = regex1 + regexInsideMethodBraces + "[;]{0,1}";
		String regex12 = regex2 + "[(][)]";

		boolean r1 = text.matches(regex1);
		boolean r2 = text.matches(regex2);
		boolean r3 = text.matches(regex3);
		boolean r4 = text.matches(regex4);
		boolean r5 = text.matches(regex5);
		boolean r6 = text.matches(regex6);
		boolean r7 = text.matches(regex7);
		boolean r8 = text.matches(regex8);
		boolean r9 = text.matches(regex9);
		boolean r10 = text.matches(regex10);
		boolean r11 = text.matches(regex11);
		boolean r12 = text.matches(regex12);
		if (r1 || r2 || r3 || r4 || r5 || r6 || r7 || r8 || r9 || r10 || r11
				|| r12)
			return true;

		return containsCamelCase;
	}





	public ArrayList<String> getTags() {
		ArrayList<String> tags = new ArrayList<>();
		String toExtractTag = summary;
		while (toExtractTag.contains("[") && toExtractTag.contains("]")) {
			int startIndex = toExtractTag.indexOf("[");
			int endIndex = toExtractTag.indexOf("]") + 1;
			String tag = toExtractTag.substring(startIndex, endIndex);
			tags.add(tag);
			toExtractTag = toExtractTag
					.substring(toExtractTag.indexOf("]") + 1);
		}
		return tags;
	}

	/**
	 * Returns a String with all comments.
	 * 
	 * @return
	 */
	public String getAllComments() {
		String concatinatedComments = "";
		for (CommentModel comment : comments) {
			String commentContent = comment.getContent();
			concatinatedComments = concatinatedComments.concat(" "
					+ commentContent);
		}
		return concatinatedComments;
	}


	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<CommentModel> getComments() {
		return comments;
	}

	public int getId() {
		return id;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setComments(ArrayList<CommentModel> comments) {
		this.comments = comments;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Determines the class name within the handle.
	 * 
	 * @param structureHandle
	 * @return the class name
	 */
	private String getClassNameInStructureHandle(String structureHandle) {
		String className = "";
		if (structureHandle.contains("[")) {
			int index = structureHandle.indexOf("[");
			String substring = structureHandle.substring(index + 1);
			if (!substring.contains("[")) {
				if (substring.contains("~")) {
					className = substring.substring(0, substring.indexOf("~"));
				} else {
					className = substring;
				}
			} else {
				int index2 = substring.indexOf("[");
				if (substring.contains("~")) {
					int indexOfMethod = substring.indexOf("~");
					if (indexOfMethod < index2) {
						className = substring.substring(0, indexOfMethod);
					} else {
						String subsubstring = substring.substring(index2 + 1);
						className = subsubstring.substring(0,
								subsubstring.indexOf("~"));
					}
				} else {
					String subsubstring = substring.substring(index2 + 1);
					className = subsubstring;
				}
			}
		} else if (structureHandle.contains("{")) {
			int index = structureHandle.indexOf("{");

			className = structureHandle.substring(index + 1,
					structureHandle.lastIndexOf("."));

		}
		return className;
	}

	/**
	 * Determines the method within a structure handle.
	 * 
	 * @param structureHandle
	 * @return the methods name
	 */
	private String getMethodNameInStrucutreHandle(String structureHandle) {
		String methodName = "";

		if (structureHandle.contains("~")) {
			int index = structureHandle.indexOf("~");
			String substring = structureHandle.substring(index + 1);
			if (substring.contains("~")) {
				methodName = substring.substring(0, substring.indexOf("~"));
			} else {
				methodName = substring;
			}
		}

		methodName = methodName.replace("[", "");

		return methodName;
	}


	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ChangeTaskModel))
			return false;

		ChangeTaskModel rhs = (ChangeTaskModel) obj;
		return new EqualsBuilder().
		// if deriving: appendSuper(super.equals(obj)).
				append(id, rhs.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
				// if deriving: appendSuper(super.hashCode()).
				append(id).toHashCode();
	}


}
