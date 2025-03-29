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

export interface EventRequest {
  title: string;
  description?: string;
  location?: string;
  status: "IN_PROGRESS" | "DONE";
  /** @format date-time */
  date: string;
  participants?: number[];
}

export interface EventResponse {
  /** @format int64 */
  id: number;
  title: string;
  description?: string;
  location?: string;
  status: "IN_PROGRESS" | "DONE";
  /** User Response */
  author: UserResponse;
  /** @format date-time */
  date: string;
  participants: UserResponse[];
  /** @format int64 */
  chatId: number;
}

/** User Response */
export interface UserResponse {
  /**
   * The ID of the user
   * @format int64
   * @example 1
   */
  id: number;
  /**
   * The username of the user
   * @example "j.doe"
   */
  username: string;
  /**
   * The name of the user
   * @example "John Doe"
   */
  name: string;
  /**
   * The email of the user
   * @example "john.doe@example.com"
   */
  email: string;
  /**
   * The role of the user
   * @example "ADMIN"
   */
  role: "USER" | "ADMIN" | "GUEST" | "HOUSE_OWNER";
}

export interface TaskRequest {
  title: string;
  assigneeUsername?: string;
  status: "IN_PROGRESS" | "DONE";
  description?: string;
  /** @format double */
  expenses?: number;
  url?: string;
}

/** Task Response */
export interface TaskResponse {
  /**
   * Task ID
   * @format int64
   */
  id: number;
  /** Task Title */
  title: string;
  /** User Response */
  assignee?: UserResponse;
  /** User Response */
  author?: UserResponse;
  /** Task Status */
  status: "IN_PROGRESS" | "DONE";
  /** Task Description */
  description?: string;
  /**
   * Expenses
   * @format double
   */
  expenses?: number;
  /** Url */
  url?: string;
}

export interface MessageRequest {
  content: string;
  /** @format int64 */
  replyToMessageId?: number;
  pinned: boolean;
}

export interface MessageResponse {
  /** @format int64 */
  id: number;
  content: string;
  /** User Response */
  author: UserResponse;
  /** @format date-time */
  timestamp: string;
  /** @format int64 */
  replyToMessageId?: number;
  pinned: boolean;
}

export interface TelegramUserRequest {
  telegramUsername: string;
}

export interface TelegramUserResponse {
  /** @format int64 */
  id: number;
  telegramUsername: string;
  /** @format int64 */
  telegramChatId?: number;
  /** User Response */
  user: UserResponse;
}

export interface ChatResponse {
  /** @format int64 */
  id: number;
  messages: MessageResponse[];
  pinnedMessages: MessageResponse[];
}

export interface SignUpRequest {
  username: string;
  password: string;
  name: string;
  email: string;
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

/** JWT Response */
export interface JwtResponse {
  /**
   * User ID
   * @format int64
   */
  id: number;
  /** Username */
  username: string;
  /** Access token */
  accessToken: string;
  /** Refresh token */
  refreshToken: string;
}

export interface SignInRequest {
  username: string;
  password: string;
}

export interface Page {
  /** @format int64 */
  totalElements?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  size?: number;
  content?: object[];
  /** @format int32 */
  number?: number;
  sort?: SortObject[];
  /** @format int32 */
  numberOfElements?: number;
  last?: boolean;
  pageable?: PageableObject;
  first?: boolean;
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

export interface PageUserResponse {
  /** @format int64 */
  totalElements?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  size?: number;
  content?: UserResponse[];
  /** @format int32 */
  number?: number;
  sort?: SortObject[];
  /** @format int32 */
  numberOfElements?: number;
  last?: boolean;
  pageable?: PageableObject;
  first?: boolean;
  empty?: boolean;
}

export interface PageEventResponse {
  /** @format int64 */
  totalElements?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  size?: number;
  content?: EventResponse[];
  /** @format int32 */
  number?: number;
  sort?: SortObject[];
  /** @format int32 */
  numberOfElements?: number;
  last?: boolean;
  pageable?: PageableObject;
  first?: boolean;
  empty?: boolean;
}

export interface PageTaskResponse {
  /** @format int64 */
  totalElements?: number;
  /** @format int32 */
  totalPages?: number;
  /** @format int32 */
  size?: number;
  content?: TaskResponse[];
  /** @format int32 */
  number?: number;
  sort?: SortObject[];
  /** @format int32 */
  numberOfElements?: number;
  last?: boolean;
  pageable?: PageableObject;
  first?: boolean;
  empty?: boolean;
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
     * No description
     *
     * @tags Event Management
     * @name GetEventById
     * @request GET:/api/events/{id}
     * @secure
     */
    getEventById: (id: number, params: RequestParams = {}) =>
      this.request<EventResponse, any>({
        path: `/api/events/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Event Management
     * @name UpdateEvent
     * @request PUT:/api/events/{id}
     * @secure
     */
    updateEvent: (id: number, data: EventRequest, params: RequestParams = {}) =>
      this.request<EventResponse, any>({
        path: `/api/events/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Event Management
     * @name DeleteEvent
     * @request DELETE:/api/events/{id}
     * @secure
     */
    deleteEvent: (id: number, params: RequestParams = {}) =>
      this.request<void, any>({
        path: `/api/events/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Task Management
     * @name GetTaskById
     * @request GET:/api/events/{eventId}/tasks/{id}
     * @secure
     */
    getTaskById: (eventId: number, id: number, params: RequestParams = {}) =>
      this.request<TaskResponse, any>({
        path: `/api/events/${eventId}/tasks/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Task Management
     * @name UpdateTask
     * @request PUT:/api/events/{eventId}/tasks/{id}
     * @secure
     */
    updateTask: (id: number, eventId: number, data: TaskRequest, params: RequestParams = {}) =>
      this.request<TaskResponse, any>({
        path: `/api/events/${eventId}/tasks/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Task Management
     * @name DeleteTask
     * @request DELETE:/api/events/{eventId}/tasks/{id}
     * @secure
     */
    deleteTask: (eventId: number, id: number, params: RequestParams = {}) =>
      this.request<void, any>({
        path: `/api/events/${eventId}/tasks/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Message Management
     * @name GetMessageById
     * @summary Get message by id
     * @request GET:/api/chats/{chatId}/messages/{id}
     * @secure
     */
    getMessageById: (chatId: number, id: number, params: RequestParams = {}) =>
      this.request<MessageResponse, any>({
        path: `/api/chats/${chatId}/messages/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Message Management
     * @name UpdateMessage
     * @summary Update message by id
     * @request PUT:/api/chats/{chatId}/messages/{id}
     * @secure
     */
    updateMessage: (chatId: number, id: number, data: MessageRequest, params: RequestParams = {}) =>
      this.request<MessageResponse, any>({
        path: `/api/chats/${chatId}/messages/${id}`,
        method: "PUT",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Message Management
     * @name DeleteMessage
     * @summary Delete message by id
     * @request DELETE:/api/chats/{chatId}/messages/{id}
     * @secure
     */
    deleteMessage: (chatId: number, id: number, params: RequestParams = {}) =>
      this.request<void, any>({
        path: `/api/chats/${chatId}/messages/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Telegram User Management
     * @name CreateTelegramUser
     * @summary Create telegram user
     * @request POST:/api/telegram-users
     * @secure
     */
    createTelegramUser: (data: TelegramUserRequest, params: RequestParams = {}) =>
      this.request<TelegramUserResponse, any>({
        path: `/api/telegram-users`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Task Management
     * @name NotifyAllClients
     * @request POST:/api/notify
     * @secure
     */
    notifyAllClients: (data: string, params: RequestParams = {}) =>
      this.request<string, any>({
        path: `/api/notify`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Event Management
     * @name GetEvents
     * @summary Get all events
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
        status?: "IN_PROGRESS" | "DONE";
      },
      params: RequestParams = {},
    ) =>
      this.request<PageEventResponse, any>({
        path: `/api/events`,
        method: "GET",
        query: query,
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Event Management
     * @name CreateEvent
     * @summary Create event
     * @request POST:/api/events
     * @secure
     */
    createEvent: (data: EventRequest, params: RequestParams = {}) =>
      this.request<EventResponse, any>({
        path: `/api/events`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Task Management
     * @name GetTasks
     * @summary Get all tasks
     * @request GET:/api/events/{eventId}/tasks
     * @secure
     */
    getTasks: (
      eventId: number,
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
        pageSize?: number;
      },
      params: RequestParams = {},
    ) =>
      this.request<PageTaskResponse, any>({
        path: `/api/events/${eventId}/tasks`,
        method: "GET",
        query: query,
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Task Management
     * @name CreateTask
     * @summary Create task
     * @request POST:/api/events/{eventId}/tasks
     * @secure
     */
    createTask: (eventId: number, data: TaskRequest, params: RequestParams = {}) =>
      this.request<TaskResponse, any>({
        path: `/api/events/${eventId}/tasks`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Chat Management
     * @name CreateChat
     * @summary Create chat
     * @request POST:/api/chats
     * @secure
     */
    createChat: (params: RequestParams = {}) =>
      this.request<ChatResponse, any>({
        path: `/api/chats`,
        method: "POST",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Message Management
     * @name GetAllMessagesByChatId
     * @summary Get all messages by chat id
     * @request GET:/api/chats/{chatId}/messages
     * @secure
     */
    getAllMessagesByChatId: (chatId: number, params: RequestParams = {}) =>
      this.request<MessageResponse[], any>({
        path: `/api/chats/${chatId}/messages`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Message Management
     * @name CreateMessage
     * @summary Create message
     * @request POST:/api/chats/{chatId}/messages
     * @secure
     */
    createMessage: (chatId: number, data: MessageRequest, params: RequestParams = {}) =>
      this.request<MessageResponse, any>({
        path: `/api/chats/${chatId}/messages`,
        method: "POST",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Registers a new user with provided details and generates JWT token
     *
     * @tags Authorization and Registration
     * @name Register
     * @summary User registration
     * @request POST:/api/auth/register
     */
    register: (data: SignUpRequest, params: RequestParams = {}) =>
      this.request<JwtResponse, ErrorMessage>({
        path: `/api/auth/register`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * @description Refreshes JWT token based on provided refresh token
     *
     * @tags Authorization and Registration
     * @name Refresh
     * @summary Refresh token
     * @request POST:/api/auth/refresh
     */
    refresh: (data: string, params: RequestParams = {}) =>
      this.request<JwtResponse, ErrorMessage>({
        path: `/api/auth/refresh`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * @description Authenticates user based on provided credentials and generates JWT token
     *
     * @tags Authorization and Registration
     * @name Login
     * @summary User login
     * @request POST:/api/auth/login
     */
    login: (data: SignInRequest, params: RequestParams = {}) =>
      this.request<JwtResponse, ErrorMessage>({
        path: `/api/auth/login`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Event Management
     * @name UpdateEventParticipants
     * @request PATCH:/api/events/{id}/participants
     * @secure
     */
    updateEventParticipants: (id: number, data: number[], params: RequestParams = {}) =>
      this.request<EventResponse, any>({
        path: `/api/events/${id}/participants`,
        method: "PATCH",
        body: data,
        secure: true,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * @description Get paginated users with optional fuzzy search by fullName or username
     *
     * @tags User Management
     * @name GetUsers
     * @summary Get users with pagination and search
     * @request GET:/api/users
     * @secure
     */
    getUsers: (
      query?: {
        /** @default "" */
        search?: string;
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
      },
      params: RequestParams = {},
    ) =>
      this.request<Page, PageUserResponse>({
        path: `/api/users`,
        method: "GET",
        query: query,
        secure: true,
        format: "json",
        ...params,
      }),

    /**
     * @description Get user by id
     *
     * @tags User Management
     * @name GetUserById
     * @summary Get user by id
     * @request GET:/api/users/{id}
     * @secure
     */
    getUserById: (id: number, params: RequestParams = {}) =>
      this.request<UserResponse, UserResponse>({
        path: `/api/users/${id}`,
        method: "GET",
        secure: true,
        format: "json",
        ...params,
      }),

    /**
     * @description Get current user
     *
     * @tags User Management
     * @name Me
     * @summary Get current user
     * @request GET:/api/me
     * @secure
     */
    me: (params: RequestParams = {}) =>
      this.request<UserResponse, UserResponse>({
        path: `/api/me`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Chat Management
     * @name GetChatById
     * @summary Get chat by id
     * @request GET:/api/chats/{id}
     * @secure
     */
    getChatById: (id: number, params: RequestParams = {}) =>
      this.request<ChatResponse, any>({
        path: `/api/chats/${id}`,
        method: "GET",
        secure: true,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Chat Management
     * @name DeleteChat
     * @summary Delete chat by id
     * @request DELETE:/api/chats/{id}
     * @secure
     */
    deleteChat: (id: number, params: RequestParams = {}) =>
      this.request<void, any>({
        path: `/api/chats/${id}`,
        method: "DELETE",
        secure: true,
        ...params,
      }),
  };
}
