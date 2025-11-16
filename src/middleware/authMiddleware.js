const jwt = require('jsonwebtoken');

function authMiddleware(req, res, next) {
  const authHeader = req.headers['authorization'];

  if (!authHeader) {
    return res.status(401).json({
      success: false,
      message: 'Tidak ada header Authorization'
    });
  }

  const parts = authHeader.split(' ');

  if (parts.length !== 2 || parts[0] !== 'Bearer') {
    return res.status(401).json({
      success: false,
      message: 'Format token tidak valid'
    });
  }

  const token = parts[1];

  jwt.verify(token, process.env.JWT_SECRET || 'devsecret', (err, decoded) => {
    if (err) {
      return res.status(401).json({
        success: false,
        message: 'Token tidak valid'
      });
    }

    req.userId = decoded.id;
    req.userRole = decoded.role;
    next();
  });
}

module.exports = authMiddleware;
