
FillableCell pop() {
    if (cellStack.isEmpty()) {
        return null;
    }
    count++;
    return cellStack.pop();
}