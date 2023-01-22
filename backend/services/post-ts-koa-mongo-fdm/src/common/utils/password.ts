export interface PasswordEncryptor {
    encrypt(plain: string): Promise<string>
}

export interface PasswordVerifier {
    verify(plain: string): Promise<boolean>
}
