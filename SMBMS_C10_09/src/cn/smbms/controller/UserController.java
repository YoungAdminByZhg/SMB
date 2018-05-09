package cn.smbms.controller;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

@Controller
@RequestMapping("/user")
public class UserController{
	private Logger logger = Logger.getLogger(UserController.class);
	
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	
	@RequestMapping(value="/login.html")
	public String login(){
		logger.debug("UserController welcome SMBMS==================");
		return "login";
	}
	
	@RequestMapping(value="/dologin.html",method=RequestMethod.POST)
	public String doLogin(@RequestParam String userCode,@RequestParam String userPassword,HttpServletRequest request,HttpSession session){
		logger.debug("doLogin====================================");
		//调用service方法，进行用户匹配
		User user = userService.login(userCode,userPassword);
		if(null != user){//登录成功
			//匹配密码
			if(!user.getUserPassword().equals(userPassword)){
				user = null;
				request.setAttribute("error", "密码输入错误！");
				return "login";
//				throw new RuntimeException("密码输入错误！");
			}else{
				session.setAttribute(Constants.USER_SESSION, user);
				return "redirect:/user/main.html";
			}
		}else{
			request.setAttribute("error", "用户名不存在！");
			return "login";
//			throw new RuntimeException("用户名不存在！");
		}
	}
	
	@RequestMapping(value="/main.html")
	public String main(HttpSession session){
		if(session.getAttribute(Constants.USER_SESSION) == null){
			return "redirect:/user/login.html";
		}
		return "frame";
	}
	
	@RequestMapping(value="/logout.html")
	public String logout(HttpSession session){
		//清除session
		session.removeAttribute(Constants.USER_SESSION);
		return "login";
	}
	
	@RequestMapping(value="/exlogin.html",method=RequestMethod.GET)
	public String exLogin(@RequestParam String userCode,
							@RequestParam String userPassword){
		logger.debug("exLogin====================================");
		//调用service方法，进行用户匹配
		User user = userService.login(userCode,userPassword);
		if(null == user){//登录失败
			throw new RuntimeException("用户名或者密码不正确！");
		}
		return "redirect:/user/main.html";
	}
	
	/*@ExceptionHandler(value={RuntimeException.class})
	public String handlerException(RuntimeException e,HttpServletRequest req){
		req.setAttribute("error", e);
		return "login";
	}*/
	
	@RequestMapping(value="/userlist.html")
	public String getUserList(Model model,
							@RequestParam(value="queryname",required=false) String queryUserName,
							@RequestParam(value="queryUserRole",required=false) String queryUserRole,
							@RequestParam(value="pageIndex",required=false) String pageIndex){
		logger.info("getUserList ---- > queryUserName: " + queryUserName);
		logger.info("getUserList ---- > queryUserRole: " + queryUserRole);
		logger.info("getUserList ---- > pageIndex: " + pageIndex);
		int _queryUserRole = 0;		
		List<User> userList = null;
		//设置页面容量
    	int pageSize = Constants.pageSize;
    	//当前页码
    	int currentPageNo = 1;
	
		if(queryUserName == null){
			queryUserName = "";
		}
		if(queryUserRole != null && !queryUserRole.equals("")){
			_queryUserRole = Integer.parseInt(queryUserRole);
		}
		
    	if(pageIndex != null){
    		try{
    			currentPageNo = Integer.valueOf(pageIndex);
    		}catch(NumberFormatException e){
    			return "redirect:/user/syserror.html";
    			//response.sendRedirect("syserror.jsp");
    		}
    	}	
    	//总数量（表）	
    	int totalCount	= userService.getUserCount(queryUserName,_queryUserRole);
    	//总页数
    	PageSupport pages=new PageSupport();
    	pages.setCurrentPageNo(currentPageNo);
    	pages.setPageSize(pageSize);
    	pages.setTotalCount(totalCount);
    	int totalPageCount = pages.getTotalPageCount();
    	//控制首页和尾页
    	if(currentPageNo < 1){
    		currentPageNo = 1;
    	}else if(currentPageNo > totalPageCount){
    		currentPageNo = totalPageCount;
    	}
		userList = userService.getUserList(queryUserName,_queryUserRole,currentPageNo,pageSize);
		model.addAttribute("userList", userList);
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		model.addAttribute("roleList", roleList);
		model.addAttribute("queryUserName", queryUserName);
		model.addAttribute("queryUserRole", queryUserRole);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		return "userlist";
	}
	
	@RequestMapping(value="/syserror.html")
	public String sysError(){
		return "syserror";
	}
	
	@RequestMapping(value = "/useradd.html",method=RequestMethod.GET)
	public String addUser(User user,Model model) {
		model.addAttribute("user",user);
		return "useradd";
	}
	
	@RequestMapping(value = "/useraddsave.html", method = RequestMethod.POST)
	public String addUserSave(User user , HttpSession session, HttpServletRequest request,
			@RequestParam(value = "attachs", required = false) MultipartFile[] attachs){
		String idPicPath = null;
		String workPicPath = null;
		String errorInfo = null;
		boolean flag = true;
		String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
		logger.info("uploadFile path ============= >" + path);
		for (int i = 0;i < attachs.length;i++) {
			MultipartFile attach = attachs[i];
			if (!attach.isEmpty()){
				if(i == 0){
					errorInfo = "uploadFileError";
				}else if(i == 1){
					errorInfo = "uploadWpError";
				}
				String oldFileName = attach.getOriginalFilename();	// 原文件名
				logger.info("uploadFile oldFileName ============= >" + oldFileName);
				String prefix = FilenameUtils.getExtension(oldFileName);	// 原文件后缀
				logger.debug("uploadFile prefix ================ >" + prefix);
				int filesize = 500000;
				logger.debug("uploadFile size ============== >" + attach.getSize());
				if (attach.getSize() > filesize) {
					request.setAttribute("uploadFileError", "* 上传大小不得超过500KB");
					flag = false;
				} else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") || 
						prefix.equalsIgnoreCase("jpeg") || prefix.equalsIgnoreCase("pneg")){
					String fileName = System.currentTimeMillis() +RandomUtils.nextInt(1000000) + "_Personal.jpg";
					logger.debug("new fileName ==========" + attach.getName());
					File targetFile = new File(path, fileName);
					if (!targetFile.exists()){
						targetFile.mkdirs();
					}
					// 保存
					try{
						attach.transferTo(targetFile);
					} catch (Exception e){
						e.printStackTrace();
						request.setAttribute("uploadFileError", "* 上传失败！");
						flag = false;
					}
					if(i == 0){
						idPicPath = path + File.separator + fileName;
					}else if(i == 1){
						workPicPath = path + File.separator + fileName;
					}
					logger.debug("idPicPath:" + idPicPath);
					logger.debug("workPicPath:" + workPicPath);
				}else{
					request.setAttribute("uploadFileError", "* 上传图片格式不正确");
					flag = false;
				}
			}
			
		}
		if(flag){
			user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
			user.setCreationDate(new Date());
			user.setIdPicPath(idPicPath);
			user.setWorkPicPath(workPicPath);
			if (userService.add(user)){
				return "redirect:/user/userlist.html";
			}
		}
		return "useradd";
	}
	
	@RequestMapping(value="/add.html",method=RequestMethod.GET)
	public String add(@ModelAttribute("user") User user){
		return "user/useradd";
	}
	
	@RequestMapping(value = "/add.html",method=RequestMethod.POST)
	public String addSave(@Valid User user,BindingResult bindingResult,HttpSession session) {
		if(bindingResult.hasErrors()){
			logger.debug("add user validated has error======");
			return "user/useradd";
		}
		user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		user.setCreationDate(new Date());
		if(userService.add(user)){
			return "redirect:/user/userlist.html";
		}
		return "useradd";
	}
	
	@RequestMapping(value="/usermodify.html",method=RequestMethod.GET)
	public String getUserById(@RequestParam String uid,Model model){
		logger.debug("getUserById uid ==========" + uid);
		User user = userService.getUserById(uid);
		model.addAttribute(user);
		return "usermodify";
	}
	
	@RequestMapping(value="/usermodifysave.html",method=RequestMethod.POST)
	public String modifyUserSave(User user,HttpSession session){
		logger.debug("modifyUserSave userid ==========" + user.getId());
		user.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		user.setModifyDate(new Date());
		if(userService.modify(user)){
			return "redirect:/user/userlist.html";
		}
		return "usermodify";
	}
	
	@RequestMapping(value="/view/{id}",method=RequestMethod.GET)
	public String view(@PathVariable String id,Model model){
		logger.debug("view id================" + id);
		User user = userService.getUserById(id);
		model.addAttribute(user);
		return "userview";
	}
}
