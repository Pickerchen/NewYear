package com.sen5.secure.launcher.data.interf;
import com.sen5.secure.launcher.data.entity.Message;
import com.sen5.secure.launcher.data.entity.WorkspaceData;

import java.util.List;

public interface FeedParser {
	List<Message> parse();
	List<WorkspaceData> parseHideApps();
}
