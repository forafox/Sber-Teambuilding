/* eslint-disable */
/* tslint:disable */
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

/** Event creation data */
export interface EventRequest {
  /**
   * Title of the event
   * @example "Team Building Workshop"
   */
  title: string;
  /**
   * Detailed description of the event
   * @example "Quarterly team building activities and training"
   */
  description?: string;
  /**
   * Physical or virtual location of the event
   * @example "Conference Room A or Zoom Meeting"
   */
  location?: string;
  /**
   * Current status of the event
   * @example "PLANNED"
   */
  status: "IN_PROGRESS" | "DONE" | "PLANNED" | "COMPLETED" | "CANCELLED";
  /**
   * Date and time when the event starts
   * @format date-time
   */
  date: string;
  /**
   * List of participant user IDs
   * @example [1,2,3]
   */
  participants?: number[];
}

/** Error message model */
export interface ErrorMessage {
  /**
   * @format int32
   * @example 404
   */
  statusCode: number;
  /**
   * @format date-time
   * @example "2024-04-11T12:00:00Z"
   */
  timestamp: string;
  /** @example "Resource not found" */
  description: string;
  /** @example "The requested resource could not be found" */
  message: string;
}

/** Complete event response with details and participants */
export interface EventResponse {
  /**
   * Unique identifier of the event
   * @format int64
   * @example 123
   */
  id: number;
  /**
   * Title of the event
   * @minLength 3
   * @maxLength 100
   * @example "Team Meeting"
   */
  title: string;
  /**
   * Detailed description of the event
   * @example "Weekly team sync meeting"
   */
  description?: string | null;
  /**
   * Location of the event (physical or virtual)
   * @example "Conference Room A or Zoom"
   */
  location?: string | null;
  /**
   * Current status of the event
   * @example "PLANNED"
   */
  status: "IN_PROGRESS" | "DONE" | "PLANNED" | "COMPLETED" | "CANCELLED";
  /** User response with complete user details */
  author: UserResponse;
  /**
   * Date and time when the event starts
   * @format date-time
   * @example "2023-12-15T09:00:00Z"
   */
  date: string;
  /** List of participants in the event */
  participants: UserResponse[];
  /**
   * ID of the chat associated with this event
   * @format int64
   * @example 456
   */
  chatId: number;
}

/** User response with complete user details */
export interface UserResponse {
  /**
   * Unique identifier of the user
   * @format int64
   * @min 1
   * @example 1
   */
  id: number;
  /**
   * Unique username for authentication
   * @minLength 4
   * @maxLength 32
   * @example "j.doe"
   */
  username: string;
  /**
   * Full name of the user
   * @minLength 2
   * @maxLength 100
   * @example "John Doe"
   */
  name: string;
  /**
   * Email address of the user
   * @example "john.doe@example.com"
   */
  email: string;
  /**
   * Role defining user permissions
   * @example "USER"
   */
  role: "USER" | "ADMIN" | "GUEST" | "HOUSE_OWNER";
}

/** Task details */
export interface TaskRequest {
  /**
   * Title of the task
   * @minLength 3
   * @maxLength 100
   * @example "Implement user authentication"
   */
  title: string;
  /**
   * Username of the assigned user
   * @example "john_dev"
   */
  assigneeUsername?: string | null;
  /**
   * Current status of the task
   * @example "TODO"
   */
  status: "IN_PROGRESS" | "DONE";
  /**
   * Detailed description of the task
   * @minLength 0
   * @maxLength 1000
   * @example "Implement JWT authentication for the API"
   */
  description?: string | null;
  /**
   * Expenses associated with the task
   * @format double
   * @min 0
   * @example 150.5
   */
  expenses?: number | null;
  /**
   * URL related to the task
   * @example "https://example.com/task-docs"
   */
  url?: string | null;
}

/** Task response with complete task details */
export interface TaskResponse {
  /**
   * Unique identifier of the task
   * @format int64
   * @min 1
   * @example 101
   */
  id: number;
  /**
   * Title of the task
   * @minLength 3
   * @maxLength 100
   * @example "Implement user authentication"
   */
  title: string;
  /** User response with complete user details */
  assignee?: UserResponse;
  /** User response with complete user details */
  author?: UserResponse;
  /**
   * Current status of the task
   * @example "TODO"
   */
  status: "IN_PROGRESS" | "DONE" | "TODO" | "BLOCKED";
  /**
   * Detailed description of the task
   * @minLength 0
   * @maxLength 1000
   * @example "Implement JWT authentication for the API"
   */
  description?: string | null;
  /**
   * Expenses associated with completing this task
   * @format double
   * @min 0
   * @example 150.5
   */
  expenses?: number | null;
  /**
   * URL related to the task (documentation, reference, etc.)
   * @example "https://example.com/task-docs"
   */
  url?: string | null;
}

/** Money transfer details */
export interface MoneyTransferRequest {
  /**
   * Amount of money to transfer
   * @format double
   * @example 100.5
   */
  amount: number;
  /**
   * ID of the user sending money
   * @format int64
   * @example 123
   */
  senderId: number;
  /**
   * ID of the user receiving money
   * @format int64
   * @example 456
   */
  recipientId: number;
}

/** Response object representing a money transfer between users */
export interface MoneyTransferResponse {
  /**
   * Unique identifier of the money transfer
   * @format int64
   * @example 789
   */
  id: number;
  /**
   * Amount of money transferred
   * @format double
   * @example 150.75
   */
  amount: number;
  /** User response with complete user details */
  sender: UserResponse;
  /** User response with complete user details */
  recipient: UserResponse;
  /** Complete event response with details and participants */
  eventResponse: EventResponse;
}

/** Updated message content and metadata */
export interface MessageUpdateRequest {
  /**
   * ID of the message to update
   * @format int64
   * @min 1
   * @example 456
   */
  id: number;
  /**
   * Updated text content of the message
   * @example "Updated: Let's meet at 3pm instead!"
   */
  content: string;
  /**
   * Updated ID of the message being replied to (null to remove reply)
   * @format int64
   * @example 123
   */
  replyToMessageId?: number | null;
  /**
   * Updated pinned status of the message
   * @example true
   */
  pinned: boolean;
  /** Poll update request payload */
  poll?: PollUpdateRequest;
}

/** Poll option update request payload */
export interface OptionUpdateRequest {
  /**
   * ID of the poll option to update
   * @format int64
   * @min 1
   * @example 1
   */
  id: number;
  /**
   * Updated title/text of the poll option
   * @minLength 1
   * @maxLength 100
   * @example "Strongly Agree"
   */
  title: string;
  /**
   * List of voter user IDs
   * @example [1,2,3]
   */
  voters: number[];
}

/** Poll update request payload */
export type PollUpdateRequest = {
  /**
   * ID of the poll to update
   * @format int64
   * @min 1
   * @example 101
   */
  id: number;
  /**
   * Updated title/question of the poll
   * @minLength 1
   * @maxLength 200
   * @example "Updated: Preferred meeting time options"
   */
  title: string;
  /**
   * Updated type of the poll
   * @example "MULTIPLE_CHOICE"
   */
  pollType: "SINGLE" | "MULTIPLE" | "SINGLE_CHOICE" | "MULTIPLE_CHOICE" | "OPEN_ENDED";
  /** Updated list of poll options */
  options: OptionUpdateRequest[];
} | null;

/** Message response with content and metadata */
export interface MessageResponse {
  /**
   * Unique identifier of the message
   * @format int64
   * @min 1
   * @example 456
   */
  id: number;
  /**
   * Text content of the message
   * @minLength 1
   * @maxLength 2000
   * @example "Hello team! Let's discuss the project updates."
   */
  content: string;
  /** User response with complete user details */
  author: UserResponse;
  /**
   * Timestamp when the message was created
   * @format date-time
   * @example "2023-12-15T09:00:00Z"
   */
  timestamp: string;
  /**
   * ID of the message this message replies to (null if not a reply)
   * @format int64
   * @example 123
   */
  replyToMessageId?: number | null;
  /**
   * Whether the message is pinned in the chat
   * @example false
   */
  pinned: boolean;
  /** Poll response with configuration and voting options */
  poll?: PollResponse;
}

/** Poll option response with voters information */
export interface OptionResponse {
  /**
   * Unique identifier of the poll option
   * @format int64
   * @min 1
   * @example 1
   */
  id: number;
  /**
   * Text content of the poll option
   * @minLength 1
   * @maxLength 100
   * @example "Agree"
   */
  title: string;
  /** List of users who voted for this option */
  voters: UserResponse[];
}

/** Poll response with configuration and voting options */
export type PollResponse = {
  /**
   * Unique identifier of the poll
   * @format int64
   * @min 1
   * @example 101
   */
  id: number;
  /**
   * Title/question of the poll
   * @minLength 1
   * @maxLength 200
   * @example "What's your preferred meeting time?"
   */
  title: string;
  /**
   * Type of the poll determining voting behavior
   * @example "SINGLE_CHOICE"
   */
  pollType: "SINGLE_CHOICE" | "MULTIPLE_CHOICE" | "OPEN_ENDED";
  /** Available voting options for this poll */
  options: OptionResponse[];
} | null;

/** Telegram user details */
export interface TelegramUserRequest {
  /**
   * Telegram username (without @)
   * @minLength 1
   * @maxLength 32
   * @example "johndoe"
   */
  telegramUsername: string;
}

/** Telegram user association response */
export interface TelegramUserResponse {
  /**
   * Unique identifier of the Telegram user association
   * @format int64
   * @min 1
   * @example 1
   */
  id: number;
  /**
   * Telegram username (without @)
   * @minLength 1
   * @maxLength 32
   * @example "johndoe"
   */
  telegramUsername: string;
  /**
   * Telegram chat ID for direct communication
   * @format int64
   * @example 123456789
   */
  telegramChatId?: number | null;
  /** User response with complete user details */
  user: UserResponse;
}

/** Complete chat response with messages and metadata */
export interface ChatResponse {
  /**
   * Unique identifier of the chat
   * @format int64
   * @example 123
   */
  id: number;
  /** List of messages in the chat */
  messages: MessageResponse[];
  /** List of pinned messages in the chat */
  pinnedMessages: MessageResponse[];
  /** Map of read messages by user ID */
  readMessages: Record<string, MessageResponse>;
}

/** Message content and metadata */
export interface MessageRequest {
  /**
   * Text content of the message
   * @example "Hello team! Let's discuss the project updates."
   */
  content: string;
  /**
   * ID of the message being replied to (null if not a reply)
   * @format int64
   * @example 123
   */
  replyToMessageId?: number | null;
  /**
   * Whether the message should be pinned in the chat
   * @example false
   */
  pinned: boolean;
  /** Poll creation request payload */
  poll?: PollRequest;
}

/** Poll option request payload */
export interface OptionRequest {
  /**
   * Title/text of the poll option
   * @example "Agree"
   */
  title: string;
}

/** Poll creation request payload */
export type PollRequest = {
  /**
   * Title/question of the poll
   * @minLength 1
   * @maxLength 200
   * @example "What's your preferred meeting time?"
   */
  title: string;
  /**
   * Type of the poll
   * @example "SINGLE"
   */
  pollType: "SINGLE" | "MULTIPLE";
  /** List of poll options */
  options: OptionRequest[];
} | null;

/** User registration details */
export interface SignUpRequest {
  /**
   * Unique username for the new account
   * @minLength 4
   * @maxLength 32
   * @example "john_doe"
   */
  username: string;
  /**
   * Password for the new account
   * @minLength 5
   * @maxLength 64
   * @example "P@ssw0rd123!"
   */
  password: string;
  /**
   * Full name of the user
   * @minLength 2
   * @maxLength 100
   * @example "John Doe"
   */
  name: string;
  /**
   * Email address of the user
   * @example "john.doe@example.com"
   */
  email: string;
}

/** JWT authentication response containing access and refresh tokens */
export interface JwtResponse {
  /**
   * Unique identifier of the authenticated user
   * @format int64
   * @min 1
   * @example 123
   */
  id: number;
  /**
   * Username of the authenticated user
   * @example "john_doe"
   */
  username: string;
  /**
   * JWT access token for API authorization
   * @example "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   */
  accessToken: string;
  /**
   * JWT refresh token for obtaining new access tokens
   * @example "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   */
  refreshToken: string;
}

/** User credentials */
export interface SignInRequest {
  /**
   * Unique username for authentication
   * @minLength 4
   * @maxLength 32
   * @example "john_doe"
   */
  username: string;
  /**
   * User's password for authentication
   * @minLength 5
   * @maxLength 64
   * @example "P@ssw0rd123!"
   */
  password: string;
}

export interface Page {
  /** @format int64 */
  totalElements?: number;
  /** @format int32 */
  totalPages?: number;
  first?: boolean;
  last?: boolean;
  /** @format int32 */
  size?: number;
  content?: object[];
  /** @format int32 */
  number?: number;
  sort?: SortObject[];
  pageable?: PageableObject;
  /** @format int32 */
  numberOfElements?: number;
  empty?: boolean;
}

export interface PageableObject {
  /** @format int64 */
  offset?: number;
  sort?: SortObject[];
  unpaged?: boolean;
  paged?: boolean;
  /** @format int32 */
  pageNumber?: number;
  /** @format int32 */
  pageSize?: number;
}

export interface SortObject {
  direction?: string;
  nullHandling?: string;
  ascending?: boolean;
  property?: string;
  ignoreCase?: boolean;
}

import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, HeadersDefaults, ResponseType } from "axios";
import axios from "axios";

export type QueryParamsType = Record<string | number, any>;

export interface FullRequestParams extends Omit<AxiosRequestConfig, "data" | "params" | "url" | "responseType"> {
  /** set parameter to `true` for call `securityWorker` for this request */
  secure?: boolean;
  /** request path */
  path: string;
  /** content type of request body */
  type?: ContentType;
  /** query params */
  query?: QueryParamsType;
  /** format of response (i.e. response.json() -> format: "json") */
  format?: ResponseType;
  /** request body */
  body?: unknown;
}

export type RequestParams = Omit<FullRequestParams, "body" | "method" | "query" | "path">;

export interface ApiConfig<SecurityDataType = unknown> extends Omit<AxiosRequestConfig, "data" | "cancelToken"> {
  securityWorker?: (
    securityData: SecurityDataType | null,
  ) => Promise<AxiosRequestConfig | void> | AxiosRequestConfig | void;
  secure?: boolean;
  format?: ResponseType;
}

export enum ContentType {
  Json = "application/json",
  FormData = "multipart/form-data",
  UrlEncoded = "application/x-www-form-urlencoded",
  Text = "text/plain",
}

export class HttpClient<SecurityDataType = unknown> {
  public instance: AxiosInstance;
  private securityData: SecurityDataType | null = null;
  private securityWorker?: ApiConfig<SecurityDataType>["securityWorker"];
  private secure?: boolean;
  private format?: ResponseType;

  constructor({ securityWorker, secure, format, ...axiosConfig }: ApiConfig<SecurityDataType> = {}) {
    this.instance = axios.create({ ...axiosConfig, baseURL: axiosConfig.baseURL || "http://localhost:8080" });
    this.secure = secure;
    this.format = format;
    this.securityWorker = securityWorker;
  }

  public setSecurityData = (data: SecurityDataType | null) => {
    this.securityData = data;
  };

  protected mergeRequestParams(params1: AxiosRequestConfig, params2?: AxiosRequestConfig): AxiosRequestConfig {
    const method = params1.method || (params2 && params2.method);

    return {
      ...this.instance.defaults,
      ...params1,
      ...(params2 || {}),
      headers: {
        ...((method && this.instance.defaults.headers[method.toLowerCase() as keyof HeadersDefaults]) || {}),
        ...(params1.headers || {}),
        ...((params2 && params2.headers) || {}),
      },
    };
  }

  protected stringifyFormItem(formItem: unknown) {
    if (typeof formItem === "object" && formItem !== null) {
      return JSON.stringify(formItem);
    } else {
      return `${formItem}`;
    }
  }

  protected createFormData(input: Record<string, unknown>): FormData {
    if (input instanceof FormData) {
      return input;
    }
    return Object.keys(input || {}).reduce((formData, key) => {
      const property = input[key];
      const propertyContent: any[] = property instanceof Array ? property : [property];

      for (const formItem of propertyContent) {
        const isFileType = formItem instanceof Blob || formItem instanceof File;
        formData.append(key, isFileType ? formItem : this.stringifyFormItem(formItem));
      }

      return formData;
    }, new FormData());
  }

  public request = async <T = any, _E = any>({
    secure,
    path,
    type,
    query,
    format,
    body,
    ...params
  }: FullRequestParams): Promise<AxiosResponse<T>> => {
    const secureParams =
      ((typeof secure === "boolean" ? secure : this.secure) &&
        this.securityWorker &&
        (await this.securityWorker(this.securityData))) ||
      {};
    const requestParams = this.mergeRequestParams(params, secureParams);
    const responseFormat = format || this.format || undefined;

    if (type === ContentType.FormData && body && body !== null && typeof body === "object") {
      body = this.createFormData(body as Record<string, unknown>);
    }

    if (type === ContentType.Text && body && body !== null && typeof body !== "string") {
      body = JSON.stringify(body);
    }

    return this.instance.request({
      ...requestParams,
      headers: {
        ...(requestParams.headers || {}),
        ...(type ? { "Content-Type": type } : {}),
      },
      params: query,
      responseType: responseFormat,
      data: body,
      url: path,
    });
  };
}

/**
 * @title PROSTO SBER HACK API
 * @version 1.0.0
 * @baseUrl http://localhost:8080
 *
 * Sample API
 */
export class Api<SecurityDataType extends unknown> extends HttpClient<SecurityDataType> {
  api = {
    /**
     * @description Retrieves complete details for a specific event
     *
     * @tags Event Management
     * @name GetEventById
     * @summary Get event by ID
     * @request GET:/api/events/{id}
     * @secure
     */
    getEventById: (id: number, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/events/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Updates all fields of an existing event
     *
     * @tags Event Management
     * @name UpdateEvent
     * @summary Update event
     * @request PUT:/api/events/{id}
     * @secure
     */
    updateEvent: (id: number, data: EventRequest, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/events/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Permanently removes an event
     *
     * @tags Event Management
     * @name DeleteEvent
     * @summary Delete event
     * @request DELETE:/api/events/{id}
     * @secure
     */
    deleteEvent: (id: number, params: RequestParams = {}) =>
      this.request<void, ErrorMessage>({
        path: `/api/events/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves details of a specific task
     *
     * @tags Task Management
     * @name GetTaskById
     * @summary Get task by ID
     * @request GET:/api/events/{eventId}/tasks/{id}
     * @secure
     */
    getTaskById: (eventId: number, id: number, params: RequestParams = {}) =>
      this.request<TaskResponse, ErrorMessage>({
        path: `/api/events/${eventId}/tasks/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Modifies all fields of an existing task
     *
     * @tags Task Management
     * @name UpdateTask
     * @summary Update task
     * @request PUT:/api/events/{eventId}/tasks/{id}
     * @secure
     */
    updateTask: (id: number, eventId: number, data: TaskRequest, params: RequestParams = {}) =>
      this.request<TaskResponse, ErrorMessage>({
        path: `/api/events/${eventId}/tasks/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Permanently removes a task
     *
     * @tags Task Management
     * @name DeleteTask
     * @summary Delete task
     * @request DELETE:/api/events/{eventId}/tasks/{id}
     * @secure
     */
    deleteTask: (eventId: number, id: number, params: RequestParams = {}) =>
      this.request<void, ErrorMessage>({
        path: `/api/events/${eventId}/tasks/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves a specific money transfer by its ID
     *
     * @tags Money Transfer
     * @name GetMoneyTransferById
     * @summary Get money transfer by ID
     * @request GET:/api/events/{eventId}/money-transfers/{id}
     * @secure
     */
    getMoneyTransferById: (
      eventId: number,
      id: number,
      params: RequestParams = {},
    ) =>
      this.request<MoneyTransferResponse, ErrorMessage>({
        path: `/api/events/${eventId}/money-transfers/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Modifies an existing money transfer
     *
     * @tags Money Transfer
     * @name UpdateMoneyTransfer
     * @summary Update money transfer
     * @request PUT:/api/events/{eventId}/money-transfers/{id}
     * @secure
     */
    updateMoneyTransfer: (
      eventId: number,
      id: number,
      data: MoneyTransferRequest,
      params: RequestParams = {},
    ) =>
      this.request<MoneyTransferResponse, ErrorMessage>({
        path: `/api/events/${eventId}/money-transfers/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Permanently removes a money transfer
     *
     * @tags Money Transfer
     * @name DeleteMoneyTransfer
     * @summary Delete money transfer
     * @request DELETE:/api/events/{eventId}/money-transfers/{id}
     * @secure
     */
    deleteMoneyTransfer: (
      eventId: number,
      id: number,
      params: RequestParams = {},
    ) =>
      this.request<void, ErrorMessage>({
        path: `/api/events/${eventId}/money-transfers/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves a specific message from a chat
     *
     * @tags Message Management
     * @name GetMessageById
     * @summary Get message by ID
     * @request GET:/api/chats/{chatId}/messages/{id}
     * @secure
     */
    getMessageById: (chatId: number, id: number, params: RequestParams = {}) =>
      this.request<MessageResponse, ErrorMessage>({
        path: `/api/chats/${chatId}/messages/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Modifies the content or properties of an existing message
     *
     * @tags Message Management
     * @name UpdateMessage
     * @summary Update message
     * @request PUT:/api/chats/{chatId}/messages/{id}
     * @secure
     */
    updateMessage: (chatId: number, id: number, data: MessageUpdateRequest, params: RequestParams = {}) =>
      this.request<MessageResponse, ErrorMessage>({
        path: `/api/chats/${chatId}/messages/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Permanently removes a message from a chat
     *
     * @tags Message Management
     * @name DeleteMessage
     * @summary Delete message
     * @request DELETE:/api/chats/{chatId}/messages/{id}
     * @secure
     */
    deleteMessage: (chatId: number, id: number, params: RequestParams = {}) =>
      this.request<void, ErrorMessage>({
        path: `/api/chats/${chatId}/messages/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * @description Validates an event participation token and adds the current user to the event.
     *
     * @tags Token Management
     * @name VerifyEventToken
     * @summary Verify and use participation token
     * @request POST:/api/tokens/verify
     * @secure
     */
    verifyEventToken: (data: string, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/tokens/verify`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Text,
        ...params,
      }),

    /**
     * @description Creates a secure token that can be used to join a specific event.
     *
     * @tags Token Management
     * @name CreateEventToken
     * @summary Generate event participation token
     * @request POST:/api/tokens/events/{eventId}
     * @secure
     */
    createEventToken: (eventId: number, params: RequestParams = {}) =>
      this.request<string, ErrorMessage>({
        path: `/api/tokens/events/${eventId}`,
        method: "POST",
        secure: true,
        ...params,
      }),

    /**
     * @description Applies an event template to create a new event with all associated tasks.
     *
     * @tags Template Management
     * @name ApplyTemplate
     * @summary Apply template
     * @request POST:/api/templates/events/{eventId}
     * @secure
     */
    applyTemplate: (eventId: number, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/templates/events/${eventId}`,
        method: "POST",
        secure: true,
        ...params,
      }),

    /**
     * @description Links a Telegram username with the authenticated user account.
     *
     * @tags Telegram Management
     * @name CreateTelegramUser
     * @summary Create Telegram user association
     * @request POST:/api/telegram-users
     * @secure
     */
    createTelegramUser: (data: TelegramUserRequest, params: RequestParams = {}) =>
      this.request<TelegramUserResponse, ErrorMessage>({
        path: `/api/telegram-users`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Sends a real-time notification to all connected WebSocket clients. Requires valid JWT authentication.
     *
     * @tags Notification Management
     * @name BroadcastNotification
     * @summary Broadcast notification
     * @request POST:/api/notify
     * @secure
     */
    broadcastNotification: (data: string, params: RequestParams = {}) =>
      this.request<string, ErrorMessage>({
        path: `/api/notify`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Retrieves events with optional status filtering
     *
     * @tags Event Management
     * @name GetEvents
     * @summary Get paginated list of events
     * @request GET:/api/events
     * @secure
     */
    getEvents: (
      query?: {
        /**
         * @format int32
         * @default 0
         */
        page?: number;
        /**
         * @format int32
         * @default 10
         */
        size?: number;
        /**
         * Status filter for events
         * @example "ACTIVE"
         */
        status?: "IN_PROGRESS" | "DONE";
      },
      params: RequestParams = {},
    ) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/events`,
        method: "GET",
        query: query,
        secure: true,
        ...params,
      }),

    /**
     * @description Creates an event and notifies participants via Telegram
     *
     * @tags Event Management
     * @name CreateEvent
     * @summary Create new event
     * @request POST:/api/events
     * @secure
     */
    createEvent: (data: EventRequest, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/events`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Retrieves a paginated list of tasks for a specific event
     *
     * @tags Task Management
     * @name GetTasks
     * @summary Get paginated tasks
     * @request GET:/api/events/{eventId}/tasks
     * @secure
     */
    getTasks: (
      eventId: number,
      query?: {
        /**
         * Page number (zero-based)
         * @format int32
         * @default 0
         * @example 0
         */
        page?: number;
        /**
         * Number of items per page
         * @format int32
         * @default 10
         * @example 10
         */
        pageSize?: number;
      },
      params: RequestParams = {},
    ) =>
      this.request<TaskResponse, ErrorMessage>({
        path: `/api/events/${eventId}/tasks`,
        method: "GET",
        query: query,
        secure: true,
        ...params,
      }),

    /**
     * @description Creates a new task and notifies assignee via Telegram
     *
     * @tags Task Management
     * @name CreateTask
     * @summary Create new task
     * @request POST:/api/events/{eventId}/tasks
     * @secure
     */
    createTask: (eventId: number, data: TaskRequest, params: RequestParams = {}) =>
      this.request<TaskResponse, ErrorMessage>({
        path: `/api/events/${eventId}/tasks`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Retrieves all money transfers for a specific event with pagination
     *
     * @tags Money Transfer
     * @name GetAllMoneyTransfersByEventId
     * @summary Get all money transfers for event
     * @request GET:/api/events/{eventId}/money-transfers
     * @secure
     */
    getAllMoneyTransfersByEventId: (
      eventId: number,
      query?: {
        /**
         * Page number (0-based)
         * @format int32
         * @default 0
         * @example 0
         */
        page?: number;
        /**
         * Number of items per page
         * @format int32
         * @default 20
         * @example 20
         */
        size?: number;
      },
      params: RequestParams = {},
    ) =>
      this.request<Page, ErrorMessage>({
        path: `/api/events/${eventId}/money-transfers`,
        method: "GET",
        query: query,
        secure: true,
        ...params,
      }),

    /**
     * @description Creates a new money transfer between users for a specific event
     *
     * @tags Money Transfer
     * @name CreateMoneyTransfer
     * @summary Create money transfer
     * @request POST:/api/events/{eventId}/money-transfers
     * @secure
     */
    createMoneyTransfer: (
      eventId: number,
      data: MoneyTransferRequest,
      params: RequestParams = {},
    ) =>
      this.request<MoneyTransferResponse, ErrorMessage>({
        path: `/api/events/${eventId}/money-transfers`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Generates an event based on natural language description
     *
     * @tags Event Management
     * @name CreateEventFromPrompt
     * @summary Create event from prompt
     * @request POST:/api/events/prompt
     * @secure
     */
    createEventFromPrompt: (data: string, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/events/prompt`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Creates a new chat session and returns chat details
     *
     * @tags Chat Management
     * @name CreateChat
     * @summary Create new chat
     * @request POST:/api/chats
     * @secure
     */
    createChat: (params: RequestParams = {}) =>
      this.request<ChatResponse, ErrorMessage>({
        path: `/api/chats`,
        method: "POST",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves all messages for a specific chat
     *
     * @tags Message Management
     * @name GetAllMessagesByChatId
     * @summary Get all chat messages
     * @request GET:/api/chats/{chatId}/messages
     * @secure
     */
    getAllMessagesByChatId: (chatId: number, params: RequestParams = {}) =>
      this.request<MessageResponse, ErrorMessage>({
        path: `/api/chats/${chatId}/messages`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Creates a new message in the specified chat
     *
     * @tags Message Management
     * @name CreateMessage
     * @summary Create new message
     * @request POST:/api/chats/{chatId}/messages
     * @secure
     */
    createMessage: (chatId: number, data: MessageRequest, params: RequestParams = {}) =>
      this.request<MessageResponse, ErrorMessage>({
        path: `/api/chats/${chatId}/messages`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Records that a user has read a specific message
     *
     * @tags Message Management
     * @name SetMessageRead
     * @summary Mark message as read
     * @request POST:/api/chats/{chatId}/messages/{id}/read
     * @secure
     */
    setMessageRead: (chatId: number, id: number, params: RequestParams = {}) =>
      this.request<void, ErrorMessage>({
        path: `/api/chats/${chatId}/messages/${id}/read`,
        method: "POST",
        secure: true,
        ...params,
      }),

    /**
     * @description Creates new user account and returns authentication tokens
     *
     * @tags Authorization and Registration
     * @name RegisterUser
     * @summary Register new user
     * @request POST:/api/auth/register
     */
    registerUser: (data: SignUpRequest, params: RequestParams = {}) =>
      this.request<JwtResponse, ErrorMessage>({
        path: `/api/auth/register`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Generates new access/refresh token pair using valid refresh token
     *
     * @tags Authorization and Registration
     * @name RefreshTokens
     * @summary Refresh authentication tokens
     * @request POST:/api/auth/refresh
     */
    refreshTokens: (data: string, params: RequestParams = {}) =>
      this.request<JwtResponse, ErrorMessage>({
        path: `/api/auth/refresh`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Validates user credentials and returns JWT tokens for authorization
     *
     * @tags Authorization and Registration
     * @name LoginUser
     * @summary Authenticate user
     * @request POST:/api/auth/login
     */
    loginUser: (data: SignInRequest, params: RequestParams = {}) =>
      this.request<JwtResponse, ErrorMessage>({
        path: `/api/auth/login`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Modifies the participant list for an event
     *
     * @tags Event Management
     * @name UpdateEventParticipants
     * @summary Update event participants
     * @request PATCH:/api/events/{id}/participants
     * @secure
     */
    updateEventParticipants: (id: number, data: number[], params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/events/${id}/participants`,
        method: "PATCH",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Retrieves paginated list of users with optional fuzzy search.
     *
     * @tags User Management
     * @name SearchUsers
     * @summary Search and paginate users
     * @request GET:/api/users
     * @secure
     */
    searchUsers: (
      query?: {
        /**
         * Search term (username or fullName)
         * @default ""
         * @example "john"
         */
        search?: string;
        /**
         * Page number (zero-based)
         * @format int32
         * @default 0
         * @example 0
         */
        page?: number;
        /**
         * Number of items per page
         * @format int32
         * @default 10
         * @example 10
         */
        size?: number;
      },
      params: RequestParams = {},
    ) =>
      this.request<Page, ErrorMessage>({
        path: `/api/users`,
        method: "GET",
        query: query,
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves user details by their unique identifier
     *
     * @tags User Management
     * @name GetUserById
     * @summary Get user by ID
     * @request GET:/api/users/{id}
     * @secure
     */
    getUserById: (id: number, params: RequestParams = {}) =>
      this.request<UserResponse, ErrorMessage>({
        path: `/api/users/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves a list of all available event templates
     *
     * @tags Template Management
     * @name GetAllEventTemplates
     * @summary Get all event templates
     * @request GET:/api/templates/events
     * @secure
     */
    getAllEventTemplates: (params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/templates/events`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves details of a specific event template
     *
     * @tags Template Management
     * @name GetEventTemplateById
     * @summary Get event template by ID
     * @request GET:/api/templates/events/{id}
     * @secure
     */
    getEventTemplateById: (id: number, params: RequestParams = {}) =>
      this.request<EventResponse, ErrorMessage>({
        path: `/api/templates/events/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves all task templates associated with an event template
     *
     * @tags Template Management
     * @name GetTaskTemplatesByEvent
     * @summary Get all task templates for event
     * @request GET:/api/templates/events/{eventId}/tasks
     * @secure
     */
    getTaskTemplatesByEvent: (eventId: number, params: RequestParams = {}) =>
      this.request<TaskResponse, ErrorMessage>({
        path: `/api/templates/events/${eventId}/tasks`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves details of a specific task template within an event template
     *
     * @tags Template Management
     * @name GetTaskTemplateById
     * @summary Get task template by ID
     * @request GET:/api/templates/events/{eventId}/tasks/{id}
     * @secure
     */
    getTaskTemplateById: (eventId: number, id: number, params: RequestParams = {}) =>
      this.request<TaskResponse, ErrorMessage>({
        path: `/api/templates/events/${eventId}/tasks/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves detailed information about the currently authenticated user
     *
     * @tags User Management
     * @name GetCurrentUser
     * @summary Get current authenticated user
     * @request GET:/api/me
     * @secure
     */
    getCurrentUser: (params: RequestParams = {}) =>
      this.request<UserResponse, ErrorMessage>({
        path: `/api/me`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Retrieves chat details including pinned and read messages
     *
     * @tags Chat Management
     * @name GetChatById
     * @summary Get chat by ID
     * @request GET:/api/chats/{id}
     * @secure
     */
    getChatById: (id: number, params: RequestParams = {}) =>
      this.request<ChatResponse, ErrorMessage>({
        path: `/api/chats/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * @description Permanently deletes a chat session
     *
     * @tags Chat Management
     * @name DeleteChat
     * @summary Delete chat by ID
     * @request DELETE:/api/chats/{id}
     * @secure
     */
    deleteChat: (id: number, params: RequestParams = {}) =>
      this.request<void, ErrorMessage>({
        path: `/api/chats/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),
  };
}
