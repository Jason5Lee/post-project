use crate::common::Password;

#[test]
fn test_empty_password() {
    assert_eq!(
        Password::try_from_plain("".to_string()),
        Err(("".to_string(), Password::password_empty()))
    );
}

#[test]
fn test_short_password() {
    assert_eq!(
        Password::try_from_plain("a".to_string()),
        Err(("a".to_string(), Password::password_too_short()))
    );
}

#[test]
fn test_long_password() {
    assert_eq!(
        Password::try_from_plain("a".repeat(73)),
        Err(("a".repeat(73), Password::password_too_long()))
    );
}

#[test]
fn test_proper_password() {
    assert!(Password::try_from_plain("iF@35p".to_string()).is_ok());
}
