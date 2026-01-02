I will implement the following changes to verify and fix the issues:

1. **Increase Tool Execution Limit** (`AbstractMessageHandler.java`):

   * **Goal**: Solve the "tool execution limit reached (2)" issue which is too strict for RAG tasks.

   * **Action**: Change `MAX_TOOL_EXECUTION_COUNT` from `2` to `10`.

2. **Fix Transaction Warnings** (`MessageServiceImpl.java`):

   * **Goal**: Resolve "Transaction not enabled" warnings.

   * **Action**: Add `@Transactional(rollbackFor = Exception.class)` to `saveMessageAndUpdateContext` and `saveMessage` methods.

3. **Enhance TXT Document Processing** (`TXTRagDocDocumentProcessing.java`):

   * **Goal**: Fix context loss by prepending filenames to text segments.

   * **Action**:

     * Fetch filename using `currentProcessingFileId`.

     * Prepend `【File: FileName】\n` to each segment.

     * Update splitter to `new DocumentBySentenceSplitter(1000, 50)`.

**Verification**:

* I will check for compilation errors.

* The changes directly address the user's feedback about the tool limit being too low and the document splitting context issue.

