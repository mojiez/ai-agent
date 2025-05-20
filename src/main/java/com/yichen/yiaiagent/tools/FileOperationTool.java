package com.yichen.yiaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.yichen.yiaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author mojie
 * @date 2025/5/16 15:28
 * @description: 文件操作工具调用
 */
public class FileOperationTool {
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR;

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String filename) {
        String filePath = FILE_DIR + "/" + filename;
        try {
            return FileUtil.readUtf8String(filePath);
        }catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }
    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String filename,
                            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + filename;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        }catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}
