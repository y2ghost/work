响应类型
- 类型定义在fastapi.responses包里面
- JSONResponse(默认)
- HTMLResponse
- PlainTextResponse
- RedirectResponse
- FileResponse
- StreamingResponse

默认API文档路径
- http://localhost:8000/docs

FastAPI-Dependencies
- 是指需要获取的某种特定信息

生产环境运行示例
- uvicorn main:app --host 0.0.0.0 --port 8000 --workers 4

