
@Override
public void message(String content) {
    outputStream.print(content);
    // Optionally, store the message if needed for testing
    // For example: this.lastMessage = content;
}