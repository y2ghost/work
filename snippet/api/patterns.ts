/**
 * 有些接口类用于表达设计意图所以使用了空类
 * API的基本定义示例
 */
abstract class ChatRoomApi {
    @post("/chatRooms")
    CreateChatRoom(req: CreateChatRoomRequest): ChatRoomApi;

    @get("/{id=ChatRooms/*}")
    GetChatRoom(req: GetChatRoomRequest): ChatRoomApi;

    @get("/chatRooms")
    ListChatRooms(req: ListChatRoomsRequest): ListChatRoomsResponse;

    @patch("/{resource.id=chatRooms/*}")
    UpdateChatRoom(req: UpdateChatRoomRequest): ChatRoom;

    @put("/{resource.id=chatRooms/*}")
    ReplaceChatRoom(req: ReplaceChatRoomRequest): ChatRoom;

    @delete("/{id=chatRooms/*}")
    DeleteChatRoom(req: DeleteChatRoomRequest): void;

    @post("/{parent=chatRooms/*}/messages")
    CreateMessage(req: CreateMessageRequest): Message;

    @get("/{parent=chatRooms/*}/messages")
    ListMessages(req: ListMessagesRequest): ListMessagesResponse;
}

interface CreateChatRoomRequest {
    resource: ChatRoom;
}

interface GetChatRoomRequest {
    id: string;
}

interface ListChatRoomsRequest {
    parent: string;
    filter: string;
}

interface ListChatRoomsResponse {
    results: ChatRoom[];
}

interface UpdateChatRoomRequest {
    resource: ChatRoom;
}

interface ReplaceChatRoomRequest {
    resource: ChatRoom;
}

interface DeleteChatRoomRequest {
    id: string;
}

interface CreateMessageRequest {
    parent: string;
    resource: Message;
}

interface ListMessagesRequest {
    parent: string;
    filter: string;
}

interface ListMessagesResponse {
    results: Message[];
}

class ChatRoom {
    // 省略
}

class Message {
    // 省略
}

/**
 * 部分更新或是部分获取示例
 */
abstract class ChatRoomApi {
    @get("/{id=chatRooms/*}")
    GetChatRoom(req: GetChatRoomRequest): ChatRoom;

    @patch("/{resource.id=chatRooms/*}")
    UpdateChatRoom(req: UpdateChatRoomRequest): ChatRoom;
}

type FieldMask = string[];
interface GetChatRoomRequest {
    id: string;
    fieldMask: FieldMask;
}

interface UpdateChatRoomRequest {
    resource: ChatRoom;
    fieldMask: FieldMask;
}

/**
 * 自定义方法示例
 */
abstract class EmailApi {
    static version = "v1";
    static title = "Email API";
    // ...

    @post("/{id=users/*emails/*}:send")
    SendEmail(req: SendEmailRequest): Email;

    @post("/{id=users/*/emails/*}:unsend")
    UnsendEmail(req: UnsendEmailRequest): Email;

    @post("/{id=users/*/emails/*}:undelete")
    UndeleteEmail(req: UndeleteEmailRequest): Email;

    @post("/{parent=users/*}/emails:export")
    ExportEmails(req: ExportEmailsRequest): ExportEmailsResponse;

    @post("/emailAddress:validate")
    ValidateEmailAddress(req: ValidateEmailAddressRequest): ValidateEmailAddressResponse;
}

interface SendEmailRequest {
    // 邮件数据
}

interface UnsendEmailRequest {
    id: string;
}

interface UndeleteEmailRequest {
    id: string;
}

interface ExportEmailsRequest {
    id: string;
}

interface ValidateEmailAddressRequest {
    id: string;
}

interface ExportEmailsResponse {
    // 导出数据
}

interface ValidateEmailAddressResponse {
    // 验证结果
}

interface Email {
    id: string;
    subject: string;
    content: string;
    state: string;
    deleted: boolean;
    // ...
}

/**
 * 长时间运行任务示例
 */
abstract class ChatRoomApi {
    @post("/{parent=chatRooms/*}/messages:analyze")
    AnalyzeMessages(req: AnalyzeMessagesRequest): Operation<MessageAnalysis, AnalyzeMessagesMetadata>;

    @get("/{id=operations/*}")
    GetOperation<ResultT, MetadataT>(req: GetOperationRequest): Operation<ResultT, MetadataT>;

    @get("/operations")
    ListOperations<ResultT, MetadataT>(req: ListOperationsRequest): ListOperationsResponse<ResultT, MetadataT>;

    @get("/{id=operations/*}:wait")
    WaitOperation<ResultT, MetadataT>(req: WaitOperationRequest): Operation<ResultT, MetadataT>;

    @post("/{id=operations/*}:cancel")
    CancelOperation<ResultT, MetadataT>(req: CancelOperationRequest): Operation<ResultT, MetadataT>;

    @post("/{id=operations/*}:pause")
    PauseOperation<ResultT, MetadataT>(req: PauseOperationRequest): Operation<ResultT, MetadataT>;

    @post("/{id=operations/*}:resume")
    ResumeOperation<ResultT, MetadataT>(req: ResumeOperationRequest): Operation<ResultT, MetadataT>;
}

interface Operation<ResultT, MetadataT> {
    id: string;
    done: boolean;
    expireTime: Date;
    result?: ResultT | OperationError;
    metadata?: MetadataT;
}

interface OperationError {
    code: string;
    message: string;
    details?: any;
}

interface GetOperationRequest {
    id: string;
}

interface ListOperationsRequest {
    filter: string;
}

interface ListOperationsResponse<ResultT, MetadataT> {
    results: Operation<ResultT, MetadataT>[];
}

interface WaitOperationRequest {
    id: string;
}

interface CancelOperationRequest {
    id: string;
}

interface PauseOperationRequest {
    id: string;
}

interface ResumeOperationRequest {
    id: string;
}

interface AnalyzeMessagesRequest {
    parent: string;
}

interface MessageAnalysis {
    chatRoom: string;
    messageCount: number;
    participantCount: number;
    userGradeLevels: Map<string, number>;
}

interface AnalyzeMessagesMetadata {
    chatRoom: string;
    paused: boolean;
    messagesProcessed: number;
    messagesCounted: number;
}

/**
 * 可重复执行的任务示例
 */
abstract class ChatRoomApi {
    @post("/analyzeChatRoomJobs")
    CreateAnalyzeChatRoomJob(req: CreateAnalyzeChatRoomJobRequest): AnalyzeChatRoomJob;

    @get("/analyzeChatRoomJobs")
    ListAnalyzeChatRoomJobs(req: ListAnalyzeChatRoomJobsRequest):
        ListAnalyzeChatRoomJobsResponse;

    @get("/{id=analyzeChatRoomJobs/*}")
    GetAnalyzeChatRoomJob(req: GetAnalyzeChatRoomJobRequest): AnalyzeChatRoomJob;

    @patch("/{resource.id=analyzeChatRoomJobs/*}")
    UpdateAnalyzeChatRoomJob(req: UpdateAnalyzeChatRoomJobRequest): AnalyzeChatRoomJob;

    @post("/{id=analyzeChatRoomJobs/*}:run")
    RunAnalyzeChatRoomJob(req: RunAnalyzeChatRoomJobRequest): Operation<AnalyzeChatRoomJobExecution, RunAnalyzeChatRoomJobMetadata>;

    @get("/{parent=analyzeChatRoomJobs/*}/executions")
    ListAnalyzeChatRoomJobExecutions(req: ListAnalyzeChatRoomJobExecutionsRequest): ListAnalyzeChatRoomJobExecutionsResponse;

    @get("/{id=analyzeChatRoomJobs/*/executions/*}")
    GetAnalyzeChatRoomExecution(req: GetAnalyzeChatRoomExecutionRequest): AnalyzeChatRoomExecution;
}

interface AnalyzeChatRoomJob {
    id: string;
    chatRoom: string;
    destination: string;
    compressionFormat: string;
}

interface AnalyzeChatRoomJobExecution {
    id: string;
    job: AnalyzeChatRoomJob;
    sentenceComplexity: number;
    sentiment: number;
    abuseScore: number;
}

interface CreateAnalyzeChatRoomJobRequest {
    resource: AnalyzeChatRoomJob;
}

interface ListAnalyzeChatRoomJobsRequest {
    filter: string;
}

interface ListAnalyzeChatRoomJobsResponse {
    results: AnalyzeChatRoomJob[];
}

interface GetAnalyzeChatRoomJobRequest {
    id: string;
}

interface UpdateAnalyzeChatRoomJobRequest {
    resource: AnalyzeChatRoomJob;
    fieldMask: string;
}

interface RunAnalyzeChatRoomJobRequest {
    id: string;
}

interface RunAnalyzeChatRoomJobMetadata {
    messagesProcessed: number;
    messagesCounted: number;
}

interface ListAnalyzeChatRoomJobExecutionsRequest {
    parent: string;
    filter: string;
}

interface ListAnalyzeChatRoomJobExecutionsResponse {
    results: AnalyzeChatRoomJobExecution[];
}

interface GetAnalyzeChatRoomJobRequest {
}

interface GetAnalyzeChatRoomExecutionRequest {
    // ...
}

interface AnalyzeChatRoomExecution {
    // ...
}

/**
 * 单例资源示例
 */
abstract class RideSharingApi {
    static version = "v1";
    static title = "Ride Sharing API";
    @get("/drivers")
    ListDrivers(req: ListDriversRequest): ListDriversResponse;

    @post("/drivers")
    CreateDriver(req: CreateDriverRequest): Driver;

    @get("/{id=drivers/*}")
    GetDriver(req: GetDriverRequest): Driver;

    @patch("/{resource.id=drivers/*}")
    UpdateDriver(req: UpdateDriverRequest): Driver;

    @delete("/{id=drivers/*}")
    DeleteDriver(req: DeleteDriverRequest): void;

    @get("/{id=drivers/*/location}")
    GetDriverLocation(req: GetDriverLocationRequest): DriverLocation;

    @patch("/{resource.id=drivers/*/location}")
    UpdateDriverLocation(req: UpdateDriverLocationRequest): DriverLocation;
}

// Location单独为一个singleton资源
interface Driver {
    id: string;
    name: string;
    licensePlate: string;
}

interface DriverLocation {
    id: string;
    lat: number;
    long: number;
    updateTime: Date;
}

interface CreateDriverRequest {
    // ...
}

interface GetDriverRequest {
    id: string;
}

interface UpdateDriverRequest {
    resource: Driver;
    fieldMask: FieldMask;
}

interface ListDriversRequest {
    maxPageSize: number;
    pageToken: string;
}

interface ListDriversResponse {
    results: Driver[];
    nextPageToken: string;
}

interface CreateDriver {
    driver: Driver;
}

interface DeleteDriverRequest {
    id: string;
}

interface GetDriverLocationRequest {
    id: string;
}

interface UpdateDriverLocationRequest {
    resource: DriverLocation;
    fieldMask: FieldMask;
}

/**
 * 交叉资源模型的示例
 */
interface Book {
    id: string;
    authorId: string;
    title: string;
}

interface Author {
    id: string;
    name: string;
}

interface ChangeLogEntry {
    id: string;
    targetId: string;
    targetType: string;
    description: string;
}

/**
 * 多对多关系资源关系示例
 */
abstract class GroupApi {
    static version = "v1";
    static title = "Group API";

    // ... Other methods left out for brevity.

    @post("/memberships")
    CreateMembership(req: CreateMembershipRequest): Membership;

    @get("/{id=memberships/*}")
    GetMembership(req: GetMembershipRequest): Membership;

    @patch("/{resource.id=memberships/*}")
    UpdateMembership(req: UpdateMembershipRequest): Membership;

    @delete("/{id=memberships/*}")
    DeleteMembership(req: DeleteMembershipRequest): void;

    @get("/memberships")
    ListMemberships(req: ListMembershipsRequest): ListMembershipsResponse;

    @get("/{groupId=groups/*}/users")
    ListGroupUsers(req: ListGroupUsersRequest): ListGroupUsersResponse;

    @get("/{userId=users/*}/groups")
    ListUserGroups(req: ListUserGroupsRequest): ListUserGroupsResponse;
}

interface Group {
    id: string;
    userCount: number;
    // ...
}

interface User {
    id: string;
    emailAddress: string;
    // ...
}

interface Membership {
    id: string;
    groupId: string;
    userId: string;
    role: string;
    expireTime: DateTime;
}

interface ListMembershipsRequest {
    parent: string;
    maxPageSize: number;
    pageToken: string;
}

interface ListMembershipsResponse {
    results: Membership[];
    nextPageToken: string;
}

interface CreateMembershipRequest {
    resource: Membership;
}

interface GetMembershipRequest {
    id: string;
}

interface UpdateMembershipRequest {
    resource: Membership;
    fieldMask: FieldMask;
}

interface DeleteMembershipRequest {
    id: string;
}

interface ListUserGroupsRequest {
    userId: string;
    maxPageSize: number;
    pageToken: string;
}

interface ListUserGroupsResponse {
    results: Group[];
    nextPageToken: string;
}

interface ListGroupUsersRequest {
    groupId: string;
    maxPageSize: number;
    pageToken: string;
}

interface ListGroupUsersResponse {
    results: User[];
    nextPageToken: string;
}

/**
 * 接着上面的模型，可以采用自定义添加和删除等方法示例
 */
abstract class GroupApi {
    static version = "v1";
    static title = "Group API";

    @get("{id=users/*}")
    GetUser(req: GetUserRequest): User;

    // ...

    @get("{id=groups/*}")
    GetGroup(req: GetGroupRequest): Group;

    // ...

    @post("{parent=groups/*}/users:add")
    AddGroupUser(req: AddGroupUserRequest): void;

    @post("{parent=group/*}/users:remove")
    RemoveGroupUser(req: RemoveGroupUserRequest): void;

    @get("{parent=groups/*}/users")
    ListGroupUsers(req: ListGroupUsersRequest): ListGroupUsersResponse;

    @get("{parent=users/*}/groups")
    ListUserGroups(req: ListUserGroupsRequest): ListUserGroupsResponse;
}

interface Group {
    id: string;
    userCount: number;
    // ...
}

interface User {
    id: string;
    emailAddress: string;
}

interface ListUserGroupsRequest {
    parent: string;
    maxPageSize?: number;
    pageToken?: string;
    filter?: string;
}

interface ListUserGroupsResponse {
    results: Group[];
    nextPageToken: string;
}
interface ListGroupUsersRequest {
    parent: string;
    maxPageSize?: number;
    pageToken?: string;
    filter?: string
}
interface ListGroupUsersResponse {
    results: User[];
    nextPageToken: string;
}
interface AddGroupUserRequest {
    parent: string;
    userId: string;
}
interface RemoveGroupUserRequest {
    parent: string;
    userId: string;
}

/**
 * 资源的多态性示例
 */
abstract class ChatRoomApi {
    @post("/{parent=chatRooms/*}/messages")
    CreateMessage(req: CreateMessageRequest): Message;
}

interface CreateMessageRequest {
    parent: string;
    resource: Message;
}

interface Message {
    id: string;
    sender: string;
    type: 'text' | 'image' | 'audio' | 'video';
    content: string | Media
}

interface Media {
    uri: string;
    contentType: string;
}

/**
 * 拷贝和移动接口示例
 * 尽量避免，看似简单，细节很多
 */
abstract class ChatRoomApi {
    @post("/{id=chatRooms/*}:copy")
    CopyChatRoom(req: CopyChatRoomRequest): ChatRoom;

    @post("/{id=chatRooms/*/messages/*}:move")
    MoveMessage(req: MoveMessageRequest): Message;
}

interface ChatRoom {
    id: string;
    title: string;
    // ...
}

interface Message {
    id: string;
    content: string;
    // ...
}

interface CopyChatRoomRequest {
    id: string;
    destinationParent: string;
}

interface MoveMessageRequest {
    id: string;
}

interface MoveMessage {
    id: string;
    destinationId: string;
}

/**
 * 批量处理示例
 */
abstract class ChatRoomApi {
    @post("/chatrooms:batchCreate")
    BatchCreateChatRooms(req: BatchCreateChatRoomsRequest): BatchCreateChatRoomsResponse;

    @post("/{parent=chatRooms/*}/messages:batchCreate")
    BatchCreateMessages(req: BatchCreateMessagesRequest): BatchCreateMessagesResponse;

    @get("/chatrooms:batchGet")
    BatchGetChatRooms(req: BatchGetChatRoomsRequest): BatchGetChatRoomsResponse;

    @get("/{parent=chatRooms/*}/messages:batchGet")
    BatchGetMessages(req: BatchGetMessagesRequest): BatchGetMessagesResponse;

    @post("/chatrooms:batchUpdate")
    BatchUpdateChatRooms(req: BatchUpdateChatRoomsRequest): BatchUpdateChatRoomsResponse;

    @post("/{parent=chatRooms/*}/messages:batchUpdate")
    BatchUpdateMessages(req: BatchUpdateMessagesRequest): BatchUpdateMessagesResponse;

    @post("/chatrooms:batchDelete")
    BatchDeleteChatRooms(req: BatchDeleteChatRoomsRequest): void;

    @post("/{parent=chatRooms/*}/messages:batchDelete")
    BatchDeleteMessages(req: BatchDeleteMessagesRequest): void;
}

interface CreateChatRoomRequest {
    resource: ChatRoom;
}

interface CreateMessageRequest {
    parent: string;
    resource: Message;
}

interface BatchCreateChatRoomsRequest {
    requests: CreateChatRoomRequest[];
}

interface BatchCreateMessagesRequest {
    parent: string;
    requests: CreateMessageRequest[];
}

interface BatchCreateChatRoomsResponse {
    resources: ChatRoom[];
}

interface BatchCreateMessagesResponse {
    resources: Message[];
}

interface BatchGetChatRoomsRequest {
    ids: string[];
}

interface BatchGetChatRoomsResponse {
    resources: ChatRoom[];
}

interface BatchGetMessagesRequest {
    parent: string;
    ids: string[];
}

interface BatchGetMessagesResponse {
    resources: Message[];
}

interface UpdateChatRoomRequest {
    resource: ChatRoom;
    fieldMask: FieldMask;
}

interface UpdateMessageRequest {
    resource: Message;
    fieldMask: FieldMask;
}

interface BatchUpdateChatRoomsRequest {
    parent: string;
    requests: UpdateChatRoomRequest[];
    fieldMask: FieldMask;
}

interface BatchUpdateMessagesRequest {
    requests: UpdateMessageRequest[];
    fieldMask: FieldMask;
}

interface BatchUpdateChatRoomsResponse {
    resources: ChatRoom[];
}

interface BatchUpdateMessagesResponse {
    resources: Message[];
}

interface BatchDeleteChatRoomsRequest {
    ids: string[];
}

interface BatchDeleteMessagesRequest {
    parent: string;
}

/**
 * 清洗数据示例
 */
abstract class ChatRoomApi {
    @post("/{parent=chatRooms/*}/messages:purge")
    PurgeMessages(req: PurgeMessagesRequest): PurgeMessagesResponse;
}

interface PurgeMessagesRequest {
    parent: string;
    filter: string;
    force?: boolean;
}

interface PurgeMessagesResponse {
    purgeCount: number;
    purgeSample: string[];
}

/**
 * 数据写入接口示例
 * 类似日志，只能追加
 */
abstract class ChatRoomApi {
    @post("/{parent=chatRooms/*}/statEntries:write")
    WriteChatRoomStatEntry(req: WriteChatRoomStatEntryRequest): void;

    @post("/{parent=chatRooms/*}/statEntries:batchWrite")
    BatchWriteChatRoomStatEntry(req: BatchWriteChatRoomStatEntryRequest): void;
}

interface ChatRoomStatEntry {
    name: string;
    value: number | string | boolean | null;
}

interface WriteChatRoomStatEntryRequest {
    parent: string;
    entry: ChatRoomStatEntry;
}

interface BatchWriteChatRoomStatEntryRequest {
    parent: string;
    requests: WriteChatRoomStatEntryRequest[];
}

/**
 * 分页模式示例
 */
abstract class ChatRoomApi {
    @get("/chatRooms")
    ListChatRooms(req: ListChatRoomsRequest): ListChatRoomsResponse;

    @get("/{id=chatRooms/*/messages/*/attachments/*}:read")
    ReadAttachment(req: ReadAttachmentRequest): ReadAttachmentResponse;

}

interface ListChatRoomsRequest {
    filter: string;
    pageToken: string;
    maxPageSize: number;
}

interface ListChatRoomsResponse {
    results: ChatRoom[];
    nextPageToken: string;
}

interface ReadAttachmentRequest {
    id: string;
    pageToken: string;
    maxBytes: number;
}

interface ReadAttachmentResponse {
    chunk: Attachment;
    fieldMask: FieldMask;
    nextPageToken: string;
}

/**
 * 导入导出模式示例
 */
abstract class ChatRoomApi {
    @post("/{parent=chatRooms/*}/messages:export")
    ExportMessages(req: ExportMessagesRequest): Operation<ExportMessagesResponse, ExportMessagesMetadata>;

    @post("/{parent=chatRooms/*}/messages:import")
    ImportMessages(req: ImportMessagesRequest): Operation<ImportMessagesResponse, ImportMessagesMetadata>;
}

interface ExportMessagesRequest {
    parent: string;
    outputConfig: MessageOutputConfig;
    dataDestination: DataDestination;
    filter: string;
}

interface ExportMessagesResponse {
    chatRoom: string;
    messagesExported: number;
    // ...
}

interface ExportMessagesMetadata {
    chatRoom: string;
    messagesExported: number;
    // ...
}

interface MessageOutputConfig {
    // The content type for serialization.
    // Choices: "json", "csv", undefined for default.
    contentType?: string;
    // Use ${number} for a zero-padded file ID number.
    // Content type will be appended with file extension (e.g., ".json").
    // Default: "messages-part-${number}"
    filenameTemplate?: string;
    // Undefined for no maximum file size.
    maxFileSizeMb?: number;
    // Choices: "zip", "bz2", undefined (not compressed)
    compressionFormat?: string;
}

interface DataDestination {
    // A unique identifier of the destination type (e.g., "s3" or "samba")
    type: string;
}

interface SambaDestination extends DataDestination {
    // The location of the Samba share (e.g., "smb://1.1.1.1:1234/path")
    type: 'samba';
    uri: string;
}

interface S3Destination extends DataDestination {
    type: 's3';
    bucketId: string;
    objectPrefix?: string;
}

interface ImportMessagesRequest {
    parent: string;
    inputConfig: MessageInputConfig;
    dataSource: DataSource;
}

interface ImportMessagesResponse {
    chatRoom: string;
    messagesImported: number;
}

interface ImportMessagesMetadata {
    chatRoom: string;
    messagesImported: number;
}

interface MessageInputConfig {
    // The content type of the input.
    // Choices: "json", "csv", undefined (auto-detected)
    contentType?: string;
    // Choices: "zip", "bz2", undefined (not compressed)
    compressionFormat?: string;
}

interface DataSource {
    type: string;
}

interface SambaSource extends DataSource {
    type: 'samba';
    uri: string;
}

interface S3Source extends DataSource {
    type: 's3';
    bucketId: string;
    // One or more masks in glob format (e.g., "folder/messages.*.csv")
    mask: string | string[];
}

/**
 * 伪删除模式示例
 */
abstract class ChatRoomApi {
    @delete("/{id=chatRooms/*}")
    DeleteChatRoom(req: DeleteChatRoomRequest): ChatRoom;

    @get("/chatRooms")
    ListChatRooms(req: ListChatRoomsRequest): ListChatRoomsResponse;

    @post("/{id=chatRooms/*}:expunge")
    ExpungeChatRoom(req: ExpungeChatRoomRequest): void;
}

interface ChatRoom {
    id: string;
    // ...
    deleted: boolean; // 是否伪删除
    expireTime: Date;
}

interface ListChatRoomsRequest {
    pageToken?: string;
    maxPageSize: number;
    filter?: string;
    includeDeleted?: boolean;
}

interface ListChatRoomsResponse {
    resources: ChatRoom[];
    nextPageToken: string;
}

interface ExpungeChatRoomRequest {
    id: string;
}

/**
 * 防止重复提交请求示例
 */
/**
流程概述
// 第一次更新
consumer -> server: updateChatRoom({requestId: 1234, resource: { ... }})
server -> cache: cache.get(1234)
server <- cache: 404 Not Found
server -> server: Update The Resource
// hash是请求体的内容计算，相同请求就是ID和请求体都一样哈
server -> cache: cache.set(1234, {hash: ...ChatRoom, response: ChatRoom{...}})
// 第二次缓存
consumer -> server: updateChatRoom({requestId: 1234, resource: { ... }})
server -> cache: cache.get(1234)
server <- cache: CacheValue {response: ChatRoom{....}, hash: ...}
server -> server: hassh == Request.hash (OK)
consumer <- server: ChatRoom{...}
 */
abstract class ChatRoomApi {
    @post("/chatRooms")
    CreateChatRoom(req: CreateChatRoomRequest): ChatRoom;
}

interface CreateChatRoomRequest {
    resource: ChatRoom;
    requestId?: string
}

// 防止重复更新资源函数示例
function UpdateChatRoom(req: UpdateChatRoomRequest): ChatRoom {
    if (req.requestId === undefined) {
        return ChatRoom.update(...);
    }

    const hash = crypto.createHash('sha256')
        .update(JSON.stringify(req))
        .digest('hex');
    const cachedResult = cache.get(req.requestId);

    if (!cachedResult) {
        const response = ChatRoom.update(...);
        cache.set(req.requestId, { response, hash });
        return response;
    }

    if (hash == cachedResult.hash) {
        return cachedResult.response;
    } else {
        throw new Error('409 Conflict');
    }
}

/**
 * dry-run或是仅验证模式示例
 * 就是不实际执行动作，检查是否能够正常执行
 */
abstract class ChatRoomApi {
    @post("/chatRooms")
    CreateChatRoom(req: CreateChatRoomRequest): ChatRoom;
}

interface CreateChatRoomRequest {
    resource: ChatRoom;
    validateOnly?: boolean;
}

/**
 * 支持资源修订版本示例
 */
abstract class ChatRoomApi {
    @get("/{id=chatRooms/*/messages/*}")
    GetMessage(req: GetMessageRequest): Message;

    @post("/{id=chatRooms/*/messages/*}:createRevision")
    CreateMessageRevision(req: CreateMessageRevisionRequest): Message;

    @post("/{id=chatRooms/*/messages/*}:restoreRevision")
    RestoreMessageRevision(req: RestoreMessageRevisionRequest): Message;

    @delete("/{id=chatRooms/*/messages/*}:deleteRevision")
    DeleteMessageRevision(req: DeleteMessageRevisionRequest): void;

    @get("/{id=chatRooms/*/messages/*}:listRevisions")
    ListMessageRevisions(req: ListMessageRevisionsRequest): ListMessageRevisionsResponse;
}

// 支持指定@修订版本号获取资源
GetMessage({ id: 'chatRooms/1/messages/2' });
GetMessage({ id: 'chatRooms/1/messages/2@abcde' });

// 回退版本示例
function restoreMessageRevision(messageId: string, revisionId: string): Message {
    const old = GetMessage({
        id: `${messageId}@${revisionId}`
    });
    UpdateMessage({
        resource: Object.assign(old, { id: messageId })
    });
    return CreateMessageRevision({ id: messageId });
}

interface Message {
    id: string;
    content: string;
    // ... more fields here ...
    revisionId: string;
    revisionCreateTime: Date;
}

interface GetMessageRequest {
    id: string;
}

interface CreateMessageRevisionRequest {
    id: string;
}

interface RestoreMessageRevisionRequest {
    id: string;
    revisionId: string;
}

interface DeleteMessageRevisionRequest {
    id: string;
}

interface ListMessageRevisionsRequest {
    id: string;
    maxPageSize: number;
    pageToken: string;
}

interface ListMessageRevisionsResponse {
    results: Message[];
    nextPageToken: string;
}

/**
 * 生成随机数ID示例
 */
const crypto = require('crypto');
const base32Decode = require('base32-decode');

function generateRandomId(length) {
    const b32Chars = '012345689ABCDEFGHJKMNPQRSTVWXYTZ';
    let id = '';

    for (let i = 0; i < length; i++) {
        let rnd = crypto.randomInt(0, b32Chars.length);
        id += b32Chars[rnd];
    }

    return id + getChecksumCharacter(id);
}

/**校验字符用于区分是否合法的ID */
function getChecksumCharacter(value) {
    const bytes = Buffer.from(base32Decode(value, 'Crockford'));
    const intValue = BigInt(`0x${bytes.toString('hex')}`);
    const checksumValue = Number(intValue % BigInt(37));
    const alphabet = '0123456789ABCDEFG' + 'HJKMNPQRSTVWXYZ*~$=U';
    return alphabet[Math.abs(checksumValue)];
}

console.log(generateRandomId(13));

/**
 * 客户端重试示例
 */
async function getChatRoomWithRetries(
    id: string, maxDelayMs = 32000, maxRetries = 10): Promise<ChatRoom> {
    return new Promise<ChatRoom>(async (resolve, reject) => {
        let retryCount = 0;
        let delayMs = 1000;
        while (true) {
            try {
                return resolve(GetChatRoom({ id }));
            } catch (e) {
                if (retryCount++ > maxRetries) return reject(e);
                await new Promise((resolve) => {
                    let actualDelayMs;
                    if ('Retry-After' in e.response.headers) {
                        actualDelayMs = Number(
                            e.response.headers['Retry-After']) * 1000;
                    } else {
                        actualDelayMs = delayMs + (Math.random() * 1000);
                    }
                    return setTimeout(resolve, actualDelayMs);
                });
                delayMs *= 2;
                if (delayMs > maxDelayMs) delayMs = maxDelayMs;
            }
        }
    });
}
