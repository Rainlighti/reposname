package cn.voidnet.scoresystem.share.user;

import cn.voidnet.scoresystem.share.user.exception.NotSetPasswordException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {
    @Resource
    private UserDAO userDAO;

    public User register(String studentId) {
        User user = new User();
        user.setStudentId(studentId);
        user.setType(UserType.UNKNOWN);
        return userDAO.save(user);
    }

    public boolean setPassword(Long userId, String password) {
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(password);
            userDAO.save(user);
            return true;
        } else return false;
    }

    @Transactional
    public boolean setUserType(Long userId, UserType userType) {
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setType(userType);
            userDAO.save(user);
            var oldTypeName = userType.getValue();
            var newTypeName
                    = oldTypeName.substring(0, 1).toUpperCase().toCharArray()[0]
                    + oldTypeName.substring(1).toLowerCase();
            userDAO.setUserType(userId, newTypeName);
            userDAO.save(user);
            return true;
        } else return false;
    }

    public boolean setName(Long userId, String name) {
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(name);
            userDAO.save(user);
            return true;
        } else return false;
    }


    public UserType getUserType(Long userId) {
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get().getType();
        } else return null;
    }

    public Optional<User> getUser(Long userId) {
        return userDAO.findById(userId);
    }

    public Optional<User> getUserByStudentId(String studentId) {
        return userDAO.findByStudentId(studentId);
    }

    public Boolean isEmptyPassword(Long userId) {
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get().getPassword() == null;
        } else return null;
    }

    public boolean isPasswordCorrect(String studentId, String password) {
        Optional<User> matchUser = userDAO.findByStudentId(studentId);
        if (matchUser.isPresent()) {
            var pwd = matchUser.get().getPassword();
            if (pwd != null) {
                return pwd.equals(password);
            } else throw new NotSetPasswordException();
        } else return false;
    }

}
