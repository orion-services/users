/**
 * Password validation utilities
 */

/**
 * Validates if password has at least 8 characters
 */
export const hasMinLength = (password) => {
  return password && password.length >= 8
}

/**
 * Validates if password has at least one uppercase letter
 */
export const hasUpperCase = (password) => {
  return password && /[A-Z]/.test(password)
}

/**
 * Validates if password has at least one lowercase letter
 */
export const hasLowerCase = (password) => {
  return password && /[a-z]/.test(password)
}

/**
 * Validates if password has at least one special character
 */
export const hasSpecialChar = (password) => {
  return password && /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
}

/**
 * Validates all password requirements and returns an object with the status of each one
 * @param {string} password - The password to be validated
 * @returns {object} Object with status of each requirement
 */
export const validatePassword = (password) => {
  if (!password) {
    return {
      minLength: false,
      upperCase: false,
      lowerCase: false,
      specialChar: false,
      isValid: false
    }
  }

  const minLength = hasMinLength(password)
  const upperCase = hasUpperCase(password)
  const lowerCase = hasLowerCase(password)
  const specialChar = hasSpecialChar(password)

  return {
    minLength,
    upperCase,
    lowerCase,
    specialChar,
    isValid: minLength && upperCase && lowerCase && specialChar
  }
}

/**
 * Returns array of validation rules for Vuetify
 * @returns {Array<Function>} Array of validation functions
 */
export const getPasswordRules = () => {
  return [
    v => !!v || 'Password is required',
    v => hasMinLength(v) || 'Password must be at least 8 characters',
    v => hasUpperCase(v) || 'Password must contain at least one uppercase letter',
    v => hasLowerCase(v) || 'Password must contain at least one lowercase letter',
    v => hasSpecialChar(v) || 'Password must contain at least one special character'
  ]
}

