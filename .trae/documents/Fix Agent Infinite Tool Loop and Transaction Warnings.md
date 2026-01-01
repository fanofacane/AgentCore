I will implement the following changes to fix the reported issues:

1. **Enhance TXT Document Processing** (`TXTRagDocDocumentProcessing.java`):

   * **Goal**: Fix context loss in document splitting by prepending the filename to each text segment.

   * **Action**: In `processFile` method:

     * Use `currentProcessingFileId` to fetch the `FileDetailEntity` and extract the filename.

     * Create a prefix string (e.g., "【File: FileName】\n").

     * Update the splitting parameters to `new DocumentBySentenceSplitter(1000, 50)` as requested.

     * Prepend the prefix to each `textSegment.text()` before storing it in `ocrData`.

2. **Prevent Agent Infinite Tool Loops** (`AbstractMessageHandler.java`):

   * **Goal**: Stop the agent from calling tools indefinitely.

   * **Action**: In `buildStreamingAgent` method:

     * Implement a proxy wrapper for `ToolExecutor`s.

     * Add a counter (`AtomicInteger`) for each request.

     * Set a hard limit (e.g., 10 calls).

     * If the limit is reached, return a system message instructing the agent to stop and answer.

3. **Fix Transaction Warnings** (`MessageServiceImpl.java`):

   * **Goal**: Resolve "Transaction not enabled" warnings.

   * **Action**: Add `@Transactional(rollbackFor = Exception.class)` to `saveMessageAndUpdateContext` and `saveMessage` methods.

**Verification**:

* I will use `GetDiagnostics` to ensure no compilation errors are introduced.

* The logic changes are straightforward and directly address the user's specific requests.

